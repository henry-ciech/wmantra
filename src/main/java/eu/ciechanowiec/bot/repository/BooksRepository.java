package eu.ciechanowiec.bot.repository;

import eu.ciechanowiec.bot.model.Book;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BooksRepository extends CrudRepository<Book, Long> {
}
