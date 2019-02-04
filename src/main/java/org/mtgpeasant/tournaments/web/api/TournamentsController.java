package org.mtgpeasant.tournaments.web.api;

import io.swagger.annotations.*;
import org.mtgpeasant.tournaments.domain.Player;
import org.mtgpeasant.tournaments.domain.Tournament;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.domain.exceptions.BadStateException;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;
import org.mtgpeasant.tournaments.domain.exceptions.UnauthorizedException;
import org.mtgpeasant.tournaments.respository.PlayerRepository;
import org.mtgpeasant.tournaments.respository.TournamentRepository;
import org.mtgpeasant.tournaments.service.TournamentEngine;
import org.mtgpeasant.tournaments.web.api.model.DeckInfo;
import org.mtgpeasant.tournaments.web.api.model.MatchResult;
import org.mtgpeasant.tournaments.web.api.model.TournamentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/tournaments", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(tags = "Tournaments API", description = "This API manages tournaments.")
public class TournamentsController extends BaseOAuthController {

    @Autowired
    private TournamentEngine tournamentEngine;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @ApiOperation("Lists tournaments, sorted by date (descending)")
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Page<Tournament> list(
            @ApiParam("pagination page number (zero-based)")
            @RequestParam(name = "page", required = false, defaultValue = "0")
                    int page,
            @ApiParam("pagination page size")
            @RequestParam(name = "size", required = false, defaultValue = "10")
                    int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("dateTime")));
        return tournamentRepository.findAll(pageable);
    }

    @ApiOperation("Get a tournament")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tournament successfully returned"),
            @ApiResponse(code = 404, message = "Tournament not found"),
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Tournament get(
            @ApiParam("tournament id")
            @PathVariable("id") Long id
    ) {
        return tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
    }

    @ApiOperation(value = "Creates a tournament", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 201, message = "Tournament successfully created"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
    })
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Tournament create(
            @ApiIgnore
                    HttpServletRequest request,
            @RequestBody TournamentInfo info
    ) {
        User user = checkIsAuthenticated(request);
        Tournament tournament = Tournament.builder()
                .owner(user)
                .name(info.getName())
                .format(info.getFormat())
                .type(info.getType())
                .location(info.getLocation())
                .locationAddress(info.getLocationAddress())
                .dateTime(info.getDateTime())
                .build();
        // TODO: return location header
        return tournamentRepository.save(tournament);
    }

    @ApiOperation(value = "Updates a tournament", notes = "Only allowed if the tournament is not started yet ('Pending' state)", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tournament successfully updated"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
            @ApiResponse(code = 403, message = "Forbidden (only allowed to the tournament creator)"),
            @ApiResponse(code = 404, message = "Tournament not found"),
            @ApiResponse(code = 409, message = "Tournament is not longer in 'Pending' state"),
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Tournament update(
            @ApiIgnore
                    HttpServletRequest request,
            @ApiParam("tournament id")
            @PathVariable("id") Long id,
            @RequestBody TournamentInfo info) {
        User user = checkIsAuthenticated(request);
        // 1: load
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
        // 2: check owner matches
        checkCanEdit(tournament, user);
        // 3: check state
        checkState(tournament, Tournament.State.Pending, "Updating a tournament is only allowed on a 'pending' tournament.");
        // 4: update &  save
        tournament.setName(info.getName());
        tournament.setFormat(info.getFormat());
        tournament.setType(info.getType());
        tournament.setLocation(info.getLocation());
        tournament.setLocationAddress(info.getLocationAddress());
        tournament.setDateTime(info.getDateTime());
        return tournamentRepository.save(tournament);
    }

    @ApiOperation(value = "Delete a tournament", notes = "Only allowed if the tournament is not started yet ('Pending' state)", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tournament successfully updated"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
            @ApiResponse(code = 403, message = "Forbidden (only allowed to the tournament creator)"),
            @ApiResponse(code = 404, message = "Tournament not found"),
            @ApiResponse(code = 409, message = "Tournament is not longer in 'Pending' state"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delete(
            @ApiIgnore
                    HttpServletRequest request,
            @ApiParam("tournament id")
            @PathVariable("id") Long id
    ) {
        User user = checkIsAuthenticated(request);
        // 1: load
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
        // 2: check owner matches
        checkCanEdit(tournament, user);
        // 3: check state
        checkState(tournament, Tournament.State.Pending, "Deleting a tournament is only allowed on a 'pending' tournament.");
        // 4: delete
        tournamentRepository.delete(tournament);
    }

    @ApiOperation(value = "Adds a player to a tournament", notes = "Only allowed if the tournament is not started yet ('Pending' state)", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tournament successfully deleted"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
            @ApiResponse(code = 403, message = "Forbidden (only allowed to the tournament creator)"),
            @ApiResponse(code = 404, message = "Tournament or player not found"),
            @ApiResponse(code = 409, message = "Tournament is not longer in 'Pending' state"),
    })
    @PutMapping("/{id}/players/{player_id}")
    @ResponseStatus(HttpStatus.OK)
    public Tournament addPlayer(
            @ApiIgnore
                    HttpServletRequest request,
            @ApiParam("tournament id")
            @PathVariable("id") Long id,
            @ApiParam("player id")
            @PathVariable("player_id") String playerId
    ) {
        User user = checkIsAuthenticated(request);
        // load tournament
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
        // 2: check owner matches
        checkCanEdit(tournament, user);
        // 3: check state
        checkState(tournament, Tournament.State.Pending, "Adding a participant to a tournament is only allowed on a 'pending' tournament.");
        // load player
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new NotFoundException("No player with id '" + playerId + "'"));
        tournament.getPlayers().add(player);
        return tournamentRepository.save(tournament);
    }

    @ApiOperation(value = "Removes a player from a tournament", notes = "Only allowed if the tournament is not started yet ('Pending' state)", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Player successfully removed"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
            @ApiResponse(code = 403, message = "Forbidden (only allowed to the tournament creator)"),
            @ApiResponse(code = 404, message = "Tournament not found"),
            @ApiResponse(code = 409, message = "Tournament is not longer in 'Pending' state"),
    })
    @DeleteMapping("/{id}/players/{player_id}")
    @ResponseStatus(HttpStatus.OK)
    public Tournament removePlayer(
            @ApiIgnore
                    HttpServletRequest request,
            @ApiParam("tournament id")
            @PathVariable("id") Long id,
            @ApiParam("player name")
            @PathVariable("player_id") String playerName
    ) {
        User user = checkIsAuthenticated(request);
        // load tournament
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
        // 2: check owner matches
        checkCanEdit(tournament, user);
        // 3: check state
        checkState(tournament, Tournament.State.Pending, "Adding a player to a tournament is only allowed on a 'pending' tournament.");
        // remove player
        Optional<Player> toRemove = tournament.getPlayers().stream().filter(player -> player.getName().equals(playerName)).findFirst();
        if (toRemove.isPresent()) {
            tournament.getPlayers().remove(toRemove);
            return tournamentRepository.save(tournament);
        }
        return tournament;
    }

    @ApiOperation(value = "Starts a tournament", notes = "Only allowed if the tournament is not started yet ('Pending' state)", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Tournament successfully started"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
            @ApiResponse(code = 403, message = "Forbidden (only allowed to the tournament creator)"),
            @ApiResponse(code = 404, message = "Tournament not found"),
            @ApiResponse(code = 409, message = "Tournament is not longer in 'Pending' state"),
    })
    @PutMapping("/{id}/state/start")
    @ResponseStatus(HttpStatus.OK)
    public Tournament startTournament(
            @ApiIgnore
                    HttpServletRequest request,
            @ApiParam("tournament id")
            @PathVariable("id") Long id
    ) {
        User user = checkIsAuthenticated(request);
        // load tournament
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
        // 2: check owner matches
        checkCanEdit(tournament, user);
        // use engine
        tournament = tournamentEngine.start(tournament);
        return tournamentRepository.save(tournament);
    }

    @ApiOperation(value = "Set a match result", notes = "Only allowed if the tournament is in progress ('InProgress' state)", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Match result successfully set"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
            @ApiResponse(code = 403, message = "Forbidden (only allowed to the tournament creator)"),
            @ApiResponse(code = 404, message = "Tournament or match not found"),
            @ApiResponse(code = 409, message = "Tournament is not longer in 'InProgress' state"),
    })
    @PutMapping("/{id}/round/{round_rank}/matches/{match_rank}")
    @ResponseStatus(HttpStatus.OK)
    public Tournament setMatchResult(
            @ApiIgnore
                    HttpServletRequest request,
            @ApiParam("tournament id")
            @PathVariable("id") Long id,
            @ApiParam("round number")
            @PathVariable("round_rank") int round,
            @ApiParam("match number")
            @PathVariable("match_rank") int match,
            @RequestBody MatchResult result
    ) {
        User user = checkIsAuthenticated(request);
        // load tournament
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
        // 2: check owner matches
        checkCanEdit(tournament, user);
        // use engine
        tournament = tournamentEngine.setMatchResult(tournament, round, match, result.getPlayerOneGamesWin(), result.getPlayerTwoGamesWin());
        return tournamentRepository.save(tournament);
    }

    @ApiOperation(value = "Set a player's deck", notes = "Can be done anytime", authorizations = @Authorization(value = "oauth2", scopes = @AuthorizationScope(scope = "api", description = "The global API access scope")))
    @ApiResponses({
            @ApiResponse(code = 200, message = "Deck successfully set"),
            @ApiResponse(code = 401, message = "Request unauthorized (needs a valid OAuth token)"),
            @ApiResponse(code = 403, message = "Forbidden (only allowed to the tournament creator)"),
            @ApiResponse(code = 404, message = "Tournament or player not found"),
    })
    @PutMapping("/{id}/players/{player_id}/deck")
    @ResponseStatus(HttpStatus.OK)
    public void setDeck(
            @ApiIgnore
                    HttpServletRequest request,
            @ApiParam("tournament id")
            @PathVariable("id") Long id,
            @ApiParam("player id")
            @PathVariable("player_id") String playerId,
            @RequestBody DeckInfo deck
    ) {
        User user = checkIsAuthenticated(request);
        // load tournament
        Tournament tournament = tournamentRepository.findById(id).orElseThrow(() -> new NotFoundException("No tournament with id " + id));
        Player player = tournament.getPlayer(playerId);
        // 2: check user is allowed
        if (!(tournament.getOwner().equals(user) || user.equals(player.getUser()))) {
            throw new UnauthorizedException("This operation is only allowed to the tournament owner (" + tournament.getOwner().getPseudo() + ") or the player (" + playerId + ")");
        }
        // TODO: save deck
    }

    private void checkState(Tournament tournament, Tournament.State state, String message) {
        if (tournament.getState() != state) {
            throw new BadStateException(message);
        }
    }

    private void checkCanEdit(Tournament tournament, User user) {
        if (!tournament.getOwner().equals(user)) {
            throw new UnauthorizedException("This operation is only allowed to the tournament owner (" + tournament.getOwner().getPseudo() + ")");
        }
    }

}
