package org.mtgpeasant.tournaments.service;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Required by {@link org.springframework.security.oauth2.provider.token.DefaultTokenServices#refreshAccessToken(String, TokenRequest)}
 */
@Service
@Slf4j
public class CustomUsersDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername('{}')", username);
        User user = userRepository.findByEmail(username).orElse(null);
        if(user == null) {
            throw new UsernameNotFoundException("Username was not found.");
        }
        return new org.springframework.security.core.userdetails.User(username, "N/A", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
