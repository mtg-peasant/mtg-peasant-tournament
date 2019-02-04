package org.mtgpeasant.tournaments.thymeleaf;

import org.mtgpeasant.tournaments.service.AuthenticationHelper;

import javax.servlet.http.HttpServletRequest;

class AuthenticationUtils {
    public boolean isAuthenticated(HttpServletRequest request) {
        return request.getUserPrincipal() != null && request.getUserPrincipal() instanceof org.springframework.security.core.Authentication && AuthenticationHelper.extractInfo((org.springframework.security.core.Authentication) request.getUserPrincipal()) != null;
    }

    public String fullName(HttpServletRequest request) {
//        return request.getUserPrincipal().getName();
        return AuthenticationHelper.extractInfo((org.springframework.security.core.Authentication) request.getUserPrincipal()).getFullname();
    }

    public String picture(HttpServletRequest request) {
        return AuthenticationHelper.extractInfo((org.springframework.security.core.Authentication) request.getUserPrincipal()).getPicture();
    }
}
