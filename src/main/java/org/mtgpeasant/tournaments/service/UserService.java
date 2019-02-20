package org.mtgpeasant.tournaments.service;

import lombok.extern.slf4j.Slf4j;
import org.mtgpeasant.tournaments.domain.User;
import org.mtgpeasant.tournaments.respository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User create(User user) throws PseudoAlreadyInUseException, EmailAlreadyInUseException {
        // check pseudo and email are not in use
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyInUseException(user.getEmail());
        }
        if (userRepository.existsByPseudo(user.getPseudo())) {
            throw new PseudoAlreadyInUseException(user.getPseudo());
        }
        // set created
        user.setCreated(Instant.now());
        // encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public static class EmailAlreadyInUseException extends Exception {
        public EmailAlreadyInUseException(String email) {
            super("The email address '" + email + "' is already in use.");
        }
    }

    public static class PseudoAlreadyInUseException extends Exception {
        public PseudoAlreadyInUseException(String pseudo) {
            super("The pseudo '" + pseudo + "' is already in use.");
        }
    }
}
