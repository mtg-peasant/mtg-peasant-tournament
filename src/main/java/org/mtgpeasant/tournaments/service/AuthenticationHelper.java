package org.mtgpeasant.tournaments.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Optional;

@Slf4j
public class AuthenticationHelper {

    public static Optional<ExtendedUsersDetailsService.ExtendedUser> userInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Authentication userAuthentication = authentication instanceof OAuth2Authentication ? ((OAuth2Authentication) authentication).getUserAuthentication() : authentication;
        if (userAuthentication.getPrincipal() instanceof ExtendedUsersDetailsService.ExtendedUser) {
            return Optional.of((ExtendedUsersDetailsService.ExtendedUser) userAuthentication.getPrincipal());
        }
        log.warn("Unexpected authentication object: {}", authentication);
        return Optional.empty();
    }

    public static String userEmail(Authentication authentication) {
        return userInfo(authentication).map(ExtendedUsersDetailsService.ExtendedUser::getEmail).orElse(null);
    }

    public static String pseudo(Authentication authentication) {
        return userInfo(authentication).map(ExtendedUsersDetailsService.ExtendedUser::getPseudo).orElse(null);
    }

    public static String fullName(Authentication authentication) {
        return userInfo(authentication).map(ExtendedUsersDetailsService.ExtendedUser::getFullName).orElse(null);
    }

    public static String pictureUrl(Authentication authentication) {
        return userInfo(authentication).map(ExtendedUsersDetailsService.ExtendedUser::getPictureUrl).orElse(null);
    }
}
