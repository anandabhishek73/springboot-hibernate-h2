package com.abhishek.demo.service;

import com.abhishek.demo.db.model.Book;
import com.abhishek.demo.db.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> list() {
        return bookRepository.findAll();
    }

//    @Transactional
    public void showAllBooks(){
        bookRepository.save(Book.builder().name("Abhishek").build());
        bookRepository.save(Book.builder().name("Himanshu").build());

        Sort.TypedSort<Book> bookSort = Sort.sort(Book.class);
        bookRepository.findAll(bookSort.by(Book::getName).ascending()
                .and(bookSort.by(Book::getAuthor).descending())
        ).forEach(book -> log.info(book.toString()));
    }
}
