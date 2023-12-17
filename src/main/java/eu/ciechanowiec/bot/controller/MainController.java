package eu.ciechanowiec.bot.controller;

import eu.ciechanowiec.bot.model.Book;
import eu.ciechanowiec.bot.service.BooksService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class MainController {

    private final BooksService booksService;

    @Autowired
    public MainController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping("/")
    ResponseEntity<List<Book>> index() {
        log.info("Received root request");
        List<Book> allBooks = booksService.findAll();
        return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }
}
