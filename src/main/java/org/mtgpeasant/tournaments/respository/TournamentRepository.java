package org.mtgpeasant.tournaments.respository;

import org.mtgpeasant.tournaments.domain.Tournament;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository class for @{@link Tournament} objects
 */
public interface TournamentRepository extends PagingAndSortingRepository<Tournament, Long> {
}
