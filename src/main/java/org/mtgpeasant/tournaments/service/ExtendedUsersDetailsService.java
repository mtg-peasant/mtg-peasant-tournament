package org.mtgpeasant.tournaments.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * {@link UserDetailsService} implementation based on {@link UserRepository}
 *
 * Required by:
 * - {@link org.mtgpeasant.tournaments.config.AuthenticationConfigurer}
 * - {@link org.springframework.security.oauth2.provider.token.DefaultTokenServices#refreshAccessToken(String, TokenRequest)}
 */
@Service
@Slf4j
public class ExtendedUsersDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername('{}')", username);
        User user = userRepository.findByEmailOrPseudo(username).orElse(null);
        if(user == null) {
            throw new UsernameNotFoundException("Username was not found.");
        }
        return ExtendedUser.from(user);
    }

    @AllArgsConstructor
    @Getter
    public static class ExtendedUser implements UserDetails, CredentialsContainer {

        private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
        public static final Set<SimpleGrantedAuthority> ROLE_USER = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        private final String email;
        private final String pseudo;
        private String password;
        private final String fullName;
        private final String pictureUrl;

        public static ExtendedUser from(User user) {
            return new ExtendedUser(user.getEmail(), user.getPseudo(), user.getPassword(), user.getFullName(), user.getPicture());
        }

        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public void eraseCredentials() {
            password = null;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // TODO: manage roles ?
            return ROLE_USER;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
