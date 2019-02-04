package org.mtgpeasant.tournaments.respository;

import org.mtgpeasant.tournaments.domain.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Repository class for @{@link User} objects
 */
public interface UserRepository extends PagingAndSortingRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPseudo(String pseudo);

    @Modifying
    @Transactional
    void deleteByEmail(String email);
}
