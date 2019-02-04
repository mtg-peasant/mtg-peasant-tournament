package org.mtgpeasant.tournaments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.*;
import org.mtgpeasant.tournaments.domain.exceptions.BadStateException;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * TODO: manage tournament type
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TournamentEngine {

    // TODO: how many players ?
    public static final int MIN_PLAYERS = 4;

    private static Random RANDOM = new Random();

    public Tournament start(Tournament tournament) {
        if (tournament.getState() != Tournament.State.Pending) {
            throw new BadStateException("Starting a tournament is only allowed on a 'pending' tournament.");
        }
        if (tournament.getPlayers().size() < MIN_PLAYERS) {
            throw new BadStateException("A tournament shall have at least " + MIN_PLAYERS + "players");
        }
        // change state
        tournament.setState(Tournament.State.InProgress);
        // set start date
        Instant now = Instant.now();
        tournament.setStarted(now);
        // compute number of rounds
        int numberOfRounds = getNumberOfRounds(tournament.getPlayers().size());
        log.info("Starting tournament with {} players: {} rounds", tournament.getPlayers().size(), numberOfRounds);

        // init scores
        for (Player player : tournament.getPlayers()) {
            tournament.getScores().add(Score.builder()
                    .id(Score.ScoreId.builder().tournament(tournament).player(player).build())
                    .build());
        }

        // init first round
        Round firstRound = Round.builder()
                .id(Round.RoundId.builder().tournament(tournament).rank(0).build())
                .type(Round.Type.Swiss)
                .started(now)
                .state(Round.State.InProgress)
                .build();

        tournament.getRounds().add(firstRound);

        // make pairings for first round
        makePairings(tournament, firstRound);

        // init other rounds
        for (int roundIdx = 1; roundIdx < numberOfRounds; roundIdx++) {
            tournament.getRounds().add(Round.builder()
                    .id(Round.RoundId.builder().tournament(tournament).rank(roundIdx).build())
                    .type(Round.Type.Swiss)
                    .state(Round.State.Pending)
                    .build());
        }

        // TODO: manage second phase if Swiss + direct elimination
        return null;
    }

    int getNumberOfRounds(int numberOfPlayers) {
        int numberOfRounds = 1;
        int maxPlayers = 2;
        while (maxPlayers < numberOfPlayers) {
            numberOfRounds++;
            maxPlayers *= 2;
        }
        return numberOfRounds;
    }

    public Tournament setMatchResult(Tournament tournament, int roundRank, int matchRank, int playerOneGamesWin, int playerTwoGamesWin) {
        if (tournament.getState() != Tournament.State.InProgress) {
            throw new BadStateException("Starting a tournament is only allowed on a tournament in progress.");
        }
        if (roundRank >= tournament.getRounds().size()) {
            throw new NotFoundException("No round #" + roundRank + " in tournament " + tournament.getId());
        }
        log.info("Setting match result round #{} match #{}: {} - {} for {}", roundRank, matchRank, playerOneGamesWin, playerTwoGamesWin, tournament);
        Round round = tournament.getRounds().get(roundRank);
        // TODO: we can also change a match round if next round has not started yet !!!
        if (round.getState() != Round.State.InProgress) {
            throw new BadStateException("Setting a match result is only allowed on an 'in progress' round.");
        }
        if(playerOneGamesWin + playerTwoGamesWin > 3) {
            throw new BadStateException("A match cannot exceed 3 games.");
        }
        // look for result
        Match match = round.getMatches().get(matchRank);
        match.setPlayerOneGamesWin(playerOneGamesWin);
        match.setPlayerTwoGamesWin(playerTwoGamesWin);
        match.setState(Match.State.Finished);

        // count unfinished matches
        long unfinished = round.getMatches().stream().filter(m -> m.getState() != Match.State.Finished).count();
        if (unfinished == 0) {
            // advance tournament state
            Instant now = Instant.now();
            round.setFinished(now);
            round.setState(Round.State.Finished);

            // compute scores
            computeScores(tournament);

            if (roundRank + 1 < tournament.getRounds().size()) {
                log.info("That was the last match of round #{}: start round #{}", roundRank, roundRank + 1);
                // next round
                Round nextRound = tournament.getRounds().get(roundRank + 1);
                nextRound.setState(Round.State.InProgress);
                nextRound.setStarted(now);

                // init next round matches (based on scores)
                makePairings(tournament, nextRound);
            } else {
                // tournament is over
                log.info("That was the last match of last round tournament is over");
                tournament.setState(Tournament.State.Finished);
                tournament.setFinished(now);
            }
        }

        return tournament;
    }

    void makePairings(Tournament tournament, Round round) {
        log.info("computing pairings for round {} on {}", round.getId().getRank(), tournament);
        // 1: sort players by score (match points)
        List<Score> sortedScores = tournament.getScores().stream()
                .sorted(Comparator.comparingInt(Score::getMatchPoints).reversed())
                .collect(Collectors.toList());

        List<Round> playedRounds = tournament.getRounds().subList(0, round.getId().getRank());
        // init matches (random)
        while (!sortedScores.isEmpty()) {
            Score score = sortedScores.remove(0);
            Player player = score.getId().getPlayer();
            List<Player> opponentsSoFar = playedRounds.stream()
                    // keep null opponent (bye)
                    .map(rd -> rd.findMatch(player).getOpponent(player))
                    .collect(Collectors.toList());
            if (sortedScores.isEmpty()) {
                // has bye
                if (opponentsSoFar.contains(null)) {
                    log.warn("player '{}' ({} match points) has the bye but already had it on turn {}", player, score.getMatchPoints(), opponentsSoFar.indexOf(null));
                } else {
                    log.info("player '{}' ({} match points) has the bye.", player, score.getMatchPoints());
                }
                round.getMatches().add(Match.builder()
                        .id(Match.MatchId.builder().round(round).rank(round.getMatches().size()).build())
                        .playerOne(player)
                        .playerTwo(null)
                        .state(Match.State.Finished)
                        .playerOneGamesWin(2)
                        .playerTwoGamesWin(0)
                        .build());
            } else {
                // now look for the next player that was not met yet
                Score opponentScore = sortedScores.stream().filter(otherScore -> !opponentsSoFar.contains(otherScore.getId().getPlayer()))
                        .findFirst().orElse(null);
                if (opponentScore == null) {
                    log.warn("No suitable opponent for player '{}' ({} match points)", player, score.getMatchPoints());
                    // pick the first
                    opponentScore = sortedScores.remove(0);
                } else {
                    sortedScores.remove(opponentScore);
                }
                log.info("player '{}' ({} match points) paired with '{}' ({} match points)", player, score.getMatchPoints(), opponentScore.getId().getPlayer(), opponentScore.getMatchPoints());
                round.getMatches().add(Match.builder()
                        .id(Match.MatchId.builder().round(round).rank(round.getMatches().size()).build())
                        .playerOne(player)
                        .playerTwo(opponentScore.getId().getPlayer())
                        .state(Match.State.InProgress)
                        .build());
            }
        }
        log.info("computed pairings for round {} on {}", round.getId().getRank(), tournament);
    }

    void computeScores(Tournament tournament) {
        log.info("computing scores for {}", tournament);
        // reset match points
        tournament.getScores().forEach(s -> s.reset());
        List<Round> playedRounds = tournament.getRounds().stream().filter(r -> r.getState() == Round.State.Finished).collect(Collectors.toList());

        // compute match points so far
        for (Round round : playedRounds) {
            for (Match match : round.getMatches()) {
                if (match.isPlayerOneWin()) {
                    tournament.getScore(match.getPlayerOne()).incrWins();
                    tournament.getScore(match.getPlayerTwo()).incrLosses();
                } else if (match.isDraw()) {
                    tournament.getScore(match.getPlayerOne()).incrDraws();
                    tournament.getScore(match.getPlayerTwo()).incrDraws();
                } else {
                    tournament.getScore(match.getPlayerOne()).incrLosses();
                    tournament.getScore(match.getPlayerTwo()).incrWins();
                }
            }
        }

        // compute player match win and game win percentages
        tournament.getScores().forEach(score -> {
            // TODO: is Max(33%, MW%) applied here or in OMW% ?
//            score.setMatchWinPercentage(Math.max(33, 100 * score.getMatchPoints() / (3 * playedRounds.size())));
            score.setMatchWinPercentage(100 * score.getMatchPoints() / (3 * playedRounds.size()));
            int playedGames = playedRounds.stream()
                    .map(round -> round.findMatch(score.getId().getPlayer()))
                    .mapToInt(match -> match.getPlayerOneGamesWin() + match.getPlayerTwoGamesWin())
                    .sum();
            int wonGames = playedRounds.stream()
                    .map(round -> round.findMatch(score.getId().getPlayer()))
                    .mapToInt(match -> match.getGamesWin(score.getId().getPlayer()))
                    .sum();
            score.setGameWinPercentage(100 * wonGames / playedGames);
        });

        // compute opponent match win and game win percentages
        tournament.getScores().forEach(score -> {
            Player player = score.getId().getPlayer();
            List<Match> playedMatches = playedRounds.stream()
                    .map(round -> round.findMatch(player))
                    // exclude bye(s)
                    .filter(Match::isNotBye)
                    .collect(Collectors.toList());
            score.setOpponentsMatchWinPercentage(playedMatches.stream()
                    .mapToInt(match -> Math.max(33, tournament.getScore(match.getOpponent(player)).getMatchWinPercentage()))
                    .sum() / playedMatches.size());
            score.setOpponentsGameWinPercentage(playedMatches.stream()
                    .mapToInt(match -> Math.max(33, tournament.getScore(match.getOpponent(player)).getGameWinPercentage()))
                    .sum() / playedMatches.size());
        });

        log.info("computed scores for {}", tournament);
    }
}
