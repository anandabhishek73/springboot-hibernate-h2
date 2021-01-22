package com.abhishek.demo.http.controller;

import com.abhishek.demo.db.projections.BookView;
import com.abhishek.demo.db.model.Book;
import com.abhishek.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/")
public class MainController {

    @Autowired
    BookService bookService;

    @Transactional
    @RequestMapping(value = "/books", produces = "application/json")
    private List<BookView> getAllBooks() {
        return bookService.getAllBooks();
    }

    @RequestMapping(value = "/books/author/{authorFirstName}", produces = "application/json")
    private List<BookView> getAllBooksForAuthor(@PathVariable String authorFirstName) {
        return bookService.getAllBooksForAuthor(authorFirstName);
    }
}
