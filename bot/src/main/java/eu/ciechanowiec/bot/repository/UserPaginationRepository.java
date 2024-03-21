package eu.ciechanowiec.bot.repository;

import eu.ciechanowiec.bot.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPaginationRepository extends PagingAndSortingRepository<User, Long> {

}
