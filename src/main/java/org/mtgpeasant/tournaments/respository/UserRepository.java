package org.mtgpeasant.tournaments.respository;

import org.mtgpeasant.tournaments.domain.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Repository class for @{@link User} objects
 */
public interface UserRepository extends PagingAndSortingRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPseudo(String pseudo);
}
