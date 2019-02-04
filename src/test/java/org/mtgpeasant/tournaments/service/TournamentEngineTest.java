package org.mtgpeasant.tournaments.service;

import org.junit.Test;
import org.mtgpeasant.tournaments.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class TournamentEngineTest {
    TournamentEngine engine = new TournamentEngine();

    @Test
    public void verify_number_of_rounds_is_okay() {
        assertThat(engine.getNumberOfRounds(4)).isEqualTo(2);
        assertThat(engine.getNumberOfRounds(5)).isEqualTo(3);
        assertThat(engine.getNumberOfRounds(6)).isEqualTo(3);
        assertThat(engine.getNumberOfRounds(7)).isEqualTo(3);
        assertThat(engine.getNumberOfRounds(8)).isEqualTo(3);
        assertThat(engine.getNumberOfRounds(9)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(10)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(11)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(12)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(13)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(14)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(15)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(16)).isEqualTo(4);
        assertThat(engine.getNumberOfRounds(17)).isEqualTo(5);
    }

    @Test
    public void replay_tournament_of_25_01_2019_should_work() {
        Player pismy = Player.builder().name("pismy").build();
        Player VeloO = Player.builder().name("VeloO").build();
        Player Laetitia = Player.builder().name("Laetitia").build();
        Player chou = Player.builder().name("chou").build();
        Player _6sco = Player.builder().name("6sco").build();
        Player Raphaël = Player.builder().name("Raphaël").build();
        Player Watthieu = Player.builder().name("Watthieu").build();
        Player Abyssal = Player.builder().name("Abyssal").build();
        Player Yann = Player.builder().name("Yann").build();
        Player Stéphane = Player.builder().name("Stéphane").build();

        Tournament tournament = Tournament.builder()
                .name("Peasant @ blastodice 25-01-2019")
                .format(MtgFormat.peasant)
                .type(Tournament.Type.Swiss)
                .location("BlatoDice")
                .dateTime(LocalDateTime.parse("2019-01-25T20:00:00"))
                .build();

        tournament.addPlayer(Stéphane);
        tournament.addPlayer(Yann);
        tournament.addPlayer(Abyssal);
        tournament.addPlayer(Watthieu);
        tournament.addPlayer(Raphaël);
        tournament.addPlayer(_6sco);
        tournament.addPlayer(chou);
        tournament.addPlayer(Laetitia);
        tournament.addPlayer(VeloO);
        tournament.addPlayer(pismy);

        engine.start(tournament);

        assertThat(tournament.getState()).isEqualTo(Tournament.State.InProgress);
        assertThat(tournament.getRounds()).hasSize(4);
        Round round1 = tournament.getRounds().get(0);
        Round round2 = tournament.getRounds().get(1);
        Round round3 = tournament.getRounds().get(2);
        Round round4 = tournament.getRounds().get(3);
        assertThat(round1.getState()).isEqualTo(Round.State.InProgress);
        assertThat(round2.getState()).isEqualTo(Round.State.Pending);
        assertThat(round3.getState()).isEqualTo(Round.State.Pending);
        assertThat(round4.getState()).isEqualTo(Round.State.Pending);

        // force 1st round
        round1.setMatches(Arrays.asList(
                Match.builder()
                        .id(Match.MatchId.builder().round(round1).build())
                        .playerOne(pismy).playerTwo(Raphaël)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round1).build())
                        .playerOne(VeloO).playerTwo(Watthieu)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round1).build())
                        .playerOne(Laetitia).playerTwo(Abyssal)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round1).build())
                        .playerOne(chou).playerTwo(Yann)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round1).build())
                        .playerOne(_6sco).playerTwo(Stéphane)
                        .state(Match.State.InProgress)
                        .build()
        ));

        // enter results
        engine.setMatchResult(tournament, 0, 0, 2, 0);
        assertThat(round1.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 0, 1, 2, 1);
        assertThat(round1.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 0, 2, 0, 2);
        assertThat(round1.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 0, 3, 0, 2);
        assertThat(round1.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 0, 4, 0, 2);
        assertThat(round1.getState()).isEqualTo(Round.State.Finished);
        assertThat(round2.getState()).isEqualTo(Round.State.InProgress);

        // assert points
        assertThat(tournament.getParticipation(pismy).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(VeloO).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(Laetitia).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(chou).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(_6sco).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(Raphaël).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(Watthieu).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(Abyssal).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(Yann).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(Stéphane).getMatchPoints()).isEqualTo(3);

        // force 2nd round
        round2.setMatches(Arrays.asList(
                Match.builder()
                        .id(Match.MatchId.builder().round(round2).build())
                        .playerOne(_6sco).playerTwo(Raphaël)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round2).build())
                        .playerOne(chou).playerTwo(Watthieu)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round2).build())
                        .playerOne(Stéphane).playerTwo(Laetitia)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round2).build())
                        .playerOne(VeloO).playerTwo(Yann)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round2).build())
                        .playerOne(pismy).playerTwo(Abyssal)
                        .state(Match.State.InProgress)
                        .build()
        ));

        // enter results
        engine.setMatchResult(tournament, 1, 0, 1, 1);
        assertThat(round2.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 1, 1, 2, 0);
        assertThat(round2.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 1, 2, 2, 1);
        assertThat(round2.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 1, 3, 1, 2);
        assertThat(round2.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 1, 4, 2, 0);
        assertThat(round2.getState()).isEqualTo(Round.State.Finished);
        assertThat(round3.getState()).isEqualTo(Round.State.InProgress);

        // assert points
        assertThat(tournament.getParticipation(pismy).getMatchPoints()).isEqualTo(6);
        assertThat(tournament.getParticipation(VeloO).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(Laetitia).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(chou).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(_6sco).getMatchPoints()).isEqualTo(1);
        assertThat(tournament.getParticipation(Raphaël).getMatchPoints()).isEqualTo(1);
        assertThat(tournament.getParticipation(Watthieu).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(Abyssal).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(Yann).getMatchPoints()).isEqualTo(6);
        assertThat(tournament.getParticipation(Stéphane).getMatchPoints()).isEqualTo(6);

        // force 3rd round
        round3.setMatches(Arrays.asList(
                Match.builder()
                        .id(Match.MatchId.builder().round(round3).build())
                        .playerOne(_6sco).playerTwo(chou)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round3).build())
                        .playerOne(Raphaël).playerTwo(Abyssal)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round3).build())
                        .playerOne(Stéphane).playerTwo(VeloO)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round3).build())
                        .playerOne(Watthieu).playerTwo(Laetitia)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round3).build())
                        .playerOne(pismy).playerTwo(Yann)
                        .state(Match.State.InProgress)
                        .build()
        ));

        // enter results
        engine.setMatchResult(tournament, 2, 0, 2, 0);
        assertThat(round3.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 2, 1, 2, 0);
        assertThat(round3.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 2, 2, 0, 2);
        assertThat(round3.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 2, 3, 1, 2);
        assertThat(round3.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 2, 4, 2, 0);
        assertThat(round3.getState()).isEqualTo(Round.State.Finished);
        assertThat(round4.getState()).isEqualTo(Round.State.InProgress);

        // assert points
        assertThat(tournament.getParticipation(pismy).getMatchPoints()).isEqualTo(9);
        assertThat(tournament.getParticipation(VeloO).getMatchPoints()).isEqualTo(6);
        assertThat(tournament.getParticipation(Laetitia).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(chou).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(_6sco).getMatchPoints()).isEqualTo(4);
        assertThat(tournament.getParticipation(Raphaël).getMatchPoints()).isEqualTo(4);
        assertThat(tournament.getParticipation(Watthieu).getMatchPoints()).isEqualTo(0);
        assertThat(tournament.getParticipation(Abyssal).getMatchPoints()).isEqualTo(3);
        assertThat(tournament.getParticipation(Yann).getMatchPoints()).isEqualTo(6);
        assertThat(tournament.getParticipation(Stéphane).getMatchPoints()).isEqualTo(6);

        // force 4th round
        round4.setMatches(Arrays.asList(
                Match.builder()
                        .id(Match.MatchId.builder().round(round4).build())
                        .playerOne(Raphaël).playerTwo(chou)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round4).build())
                        .playerOne(_6sco).playerTwo(Laetitia)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round4).build())
                        .playerOne(Stéphane).playerTwo(Watthieu)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round4).build())
                        .playerOne(Yann).playerTwo(Abyssal)
                        .state(Match.State.InProgress)
                        .build(),
                Match.builder()
                        .id(Match.MatchId.builder().round(round4).build())
                        .playerOne(pismy).playerTwo(VeloO)
                        .state(Match.State.InProgress)
                        .build()
        ));

        // enter results
        engine.setMatchResult(tournament, 3, 0, 0, 2);
        assertThat(round4.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 3, 1, 2, 0);
        assertThat(round4.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 3, 2, 2, 1);
        assertThat(round4.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 3, 3, 2, 0);
        assertThat(round4.getState()).isEqualTo(Round.State.InProgress);

        engine.setMatchResult(tournament, 3, 4, 2, 1);
        assertThat(round4.getState()).isEqualTo(Round.State.Finished);
        assertThat(tournament.getState()).isEqualTo(Tournament.State.Finished);

        // check participations and order
        assertThat(tournament.getParticipations())
                .extracting("id.player", "matchPoints")
                .containsExactly(
                        tuple(pismy, 12),
                        tuple(Yann, 9),
                        tuple(Stéphane, 9),
                        tuple(_6sco, 7),
                        tuple(VeloO, 6),
                        tuple(chou, 6),
                        tuple(Raphaël, 4),
                        tuple(Abyssal, 3),
                        tuple(Laetitia, 3),
                        tuple(Watthieu, 0)
                );
    }
}