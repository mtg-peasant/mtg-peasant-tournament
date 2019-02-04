package org.mtgpeasant.tournaments.web.api;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.domain.exceptions.UnauthorizedException;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public abstract class BaseOAuthController {
    @Autowired
    OAuth2Helper oAuth2Helper;

    @Autowired
    UserRepository userRepository;

    User checkIsAuthenticated(HttpServletRequest request) {
        String email = oAuth2Helper.extractEmail(request);
        log.debug("email from OAuth token: '{}'", email);
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("not authenticated"));
    }

}
