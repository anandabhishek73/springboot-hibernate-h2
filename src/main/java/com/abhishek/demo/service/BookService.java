package com.abhishek.demo.service;

import com.abhishek.demo.db.projections.BookView;
import com.abhishek.demo.db.model.Book;
import com.abhishek.demo.db.repository.BookRepository;
import lombok.NonNull;
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

    @Transactional
    public void showAllBooks(){
        getAllBooks().forEach(book -> log.info(book.toString()));
    }

    @Transactional
    public List<BookView> getAllBooks(){
//        Sort.TypedSort<Book> bookSort = Sort.sort(Book.class);
//        return bookRepository.findAllAndSort(bookSort.by(Book::getName).ascending()
//                .and(bookSort.by(Book::getAuthor).descending()),
//                BookView.class
//        );
        return bookRepository.findAllByIdGreaterThan(0L);
    }

    public List<BookView> getAllBooksForAuthor(@NonNull String authorFirstName){
        return bookRepository.findByAuthor_FirstName(authorFirstName);
    }

    public Book createNewBook(Book book){
        return bookRepository.save(book);
    }
}
