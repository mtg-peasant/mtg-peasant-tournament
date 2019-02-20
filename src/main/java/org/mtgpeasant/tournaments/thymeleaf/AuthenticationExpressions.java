package org.mtgpeasant.tournaments.thymeleaf;

import org.mtgpeasant.tournaments.service.AuthenticationHelper;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

class AuthenticationExpressions {
    public boolean isAuthenticated(HttpServletRequest request) {
        return request.getUserPrincipal() != null
                && request.getUserPrincipal() instanceof Authentication
                && ((Authentication) request.getUserPrincipal()).isAuthenticated();
    }

    public String fullName(HttpServletRequest request) {
        return AuthenticationHelper.fullName((Authentication) request.getUserPrincipal());
    }

    public String picture(HttpServletRequest request) {
        return AuthenticationHelper.pictureUrl((Authentication) request.getUserPrincipal());
    }
}
