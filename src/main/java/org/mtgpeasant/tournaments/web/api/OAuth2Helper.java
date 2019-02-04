package org.mtgpeasant.tournaments.web.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2Helper {
    @Autowired
    private final TokenStore tokenStore;

    OAuth2Authentication extract(HttpServletRequest request) {
        // decode token
        // throw InvalidTokenException in case token is invalid or signature cannot be verified
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorization == null) {
            log.error("missing Authorization header");
            throw new UnauthorizedException("this api requires a valid OAuth token");
        }
        authorization = authorization.trim();
        if(!authorization.toLowerCase().startsWith("bearer ")) {
            log.error("Authorization header of wrong type: {}", authorization);
            throw new UnauthorizedException("this api requires a valid OAuth token");
        }
        String token = authorization.substring(7);
        log.debug("Authorization header contains OAuth token: {}", token);
        return tokenStore.readAuthentication(token);
    }

    String extractEmail(HttpServletRequest request) {
        return extract(request).getName();
    }

}
