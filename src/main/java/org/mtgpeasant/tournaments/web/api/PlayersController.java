package org.mtgpeasant.tournaments.web.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.mtgpeasant.tournaments.domain.Player;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.domain.exceptions.AlreadyExistsException;
import org.mtgpeasant.tournaments.domain.exceptions.NotFoundException;
import org.mtgpeasant.tournaments.respository.PlayerRepository;
import org.mtgpeasant.tournaments.web.api.model.PlayerInfo;
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

/**
 * TODO: find players by name "like"
 */
@RestController
@RequestMapping(value = "/api/players", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(tags = "Players API", description = "This API manages players.")
public class PlayersController extends BaseOAuthController {

    @Autowired
    private PlayerRepository playerRepository;

    @ApiOperation("Lists players, sorted by date (descending)")
    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public Page<Player> list(
            @ApiParam("pagination page number (zero-based)")
            @RequestParam(name = "page", required = false, defaultValue = "0")
                    int page,
            @ApiParam("pagination page size")
            @RequestParam(name = "size", required = false, defaultValue = "10")
                    int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("created")));
        return playerRepository.findAll(pageable);
    }

    @ApiOperation("Get a player")
    @GetMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Player get(
            @ApiParam("player name")
            @PathVariable("name") String name
    ) {
        return playerRepository.findById(name).orElseThrow(() -> new NotFoundException("No player with name '" + name + "'"));
    }

    @ApiOperation("Creates a player")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(
            @ApiIgnore
                    HttpServletRequest request,
            @RequestBody PlayerInfo info
    ) {
        Optional<Player> playerWithSameName = playerRepository.findById(info.getName());
        if (playerWithSameName.isPresent()) {
            throw new AlreadyExistsException("Player with name '" + info.getName() + "' already exists");
        }
        User user = checkIsAuthenticated(request);
        Player player = Player.builder()
                .name(info.getName())
                .createdBy(user)
                .build();
        // TODO: return location header
        return playerRepository.save(player);
    }

//    @ApiOperation("Updates a player")
//    @PutMapping("/{name}")
//    @ResponseStatus(HttpStatus.OK)
//    public Player update(
//            Authentication auth,
//            @ApiParam("player name")
//            @PathVariable("name") String name,
//            @RequestBody PlayerInfo info)
//    {
//        User user = checkIsAuthenticated(auth);
//        // 1: load
//        Player player = playerRepository.findById(name).orElseThrow(() -> new NotFoundException("No player with name '" + name + "'"));
//        // 2: check owner matches
//        checkCanEdit(player, user);
//        // 4: update &  save
//        player.setName(info.getName());
//        return playerRepository.save(player);
//    }

//    @ApiOperation("Delete a player")
//    @DeleteMapping("/{name}")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void delete(
//            Authentication auth,
//            @ApiParam("player name")
//            @PathVariable("name") String name
//    ) {
//        User user = checkIsAuthenticated(auth);
//        // 1: load
//        Player player = playerRepository.findById(name).orElseThrow(() -> new NotFoundException("No player with name '" + name + "'"));
//        // 2: check owner matches
//        checkCanEdit(player, user);
//        // 4: delete
//        playerRepository.delete(player);
//    }

}
