package com.abhishek.demo.service;

import com.abhishek.demo.db.model.Author;
import com.abhishek.demo.db.model.Book;
import com.abhishek.demo.db.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;


    @Test
    public void whenApplicationStarts_thenHibernateCreatesInitialRecords() {
        List<Book> books = bookService.list();

        assertThat(books.size()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void whenCreateAuthorFromBookBuilder_thenHibernateShouldCreateAuthor() throws ParseException {

        Author author = Author.builder()
                .firstName("E. L.")
                .lastName("James")
                .dob(new SimpleDateFormat("dd-MM-yyyy").parse("01-02-1993"))
                .build();
        Book bookWithAuthor = Book.builder()
                .name("50 Shades of Grey")
                .ISBN("ABBAB3456")
                .author(author)
                .build();
        bookRepository.save(bookWithAuthor);

//        assertThat(bookRepository.findByAuthor_FirstName("E. L.").get(0).
//                getAuthorLastName())
//                .isEqualTo("James");
        assertThat(bookRepository.findById(bookWithAuthor.getId()).get().getAuthor())
                .isEqualTo(author);
    }

}