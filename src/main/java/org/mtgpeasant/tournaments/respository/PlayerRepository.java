package org.mtgpeasant.tournaments.respository;

import org.mtgpeasant.tournaments.domain.Player;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository class for @{@link Player} objects
 */
public interface PlayerRepository extends PagingAndSortingRepository<Player, String> {
}
