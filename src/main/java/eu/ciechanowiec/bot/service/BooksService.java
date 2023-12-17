package eu.ciechanowiec.bot.service;

import eu.ciechanowiec.bot.model.Book;
import eu.ciechanowiec.bot.repository.BooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

@Service
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    @SuppressWarnings("ChainedMethodCall")
    public List<Book> findAll() {
        Iterable<Book> allItems = booksRepository.findAll();
        Spliterator<Book> allItemsSpliterator = allItems.spliterator();
        return StreamSupport.stream(allItemsSpliterator, false)
                            .toList();
    }
}
