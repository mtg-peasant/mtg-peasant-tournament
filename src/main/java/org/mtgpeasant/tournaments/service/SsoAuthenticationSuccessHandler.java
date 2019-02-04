package org.mtgpeasant.tournaments.service;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

/**
 * Creates/updates users database after a successful SSO authentication
 */
@Service
@Slf4j
public class SsoAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("onAuthenticationSuccess: {}", authentication);
        // update user profile
        AuthenticationHelper.AuthentInfo info = AuthenticationHelper.extractInfo(authentication);
        if (info != null && info.getEmail() != null) {
            User user = userRepository.findByEmail(info.getEmail()).orElse(null);
            if (user == null) {
                user = User.builder()
                        .email(info.getEmail())
                        .created(Instant.now())
                        .fullName(info.getFullname())
                        .picture(info.getPicture())
                        // TODO
                        .pseudo(info.getFullname())
                        .build();
                user.setCreated(Instant.now());
                log.info("new user has connected: {}", user);
            } else {
                // TODO: update info ?
                log.info("existing user has connected: {}", user);
            }
            user.setLastConnected(Instant.now());
            userRepository.save(user);
        }
        // then proceed with authentication
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
