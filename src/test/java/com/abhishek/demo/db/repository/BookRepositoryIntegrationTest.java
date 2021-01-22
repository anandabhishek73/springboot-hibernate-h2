package com.abhishek.demo.db.repository;

import com.abhishek.demo.db.projections.AuthorClassProjectionView;
import com.abhishek.demo.db.projections.AuthorView;
import com.abhishek.demo.db.projections.BookView;
import com.abhishek.demo.db.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class BookRepositoryIntegrationTest {

    List<Book> activeEntries;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    private void beforeEach(){
//        bookRepository.findAll().forEach(b->log.info("Before : "+ b));
    }
    @AfterEach
    private void afterEach(){
//        bookRepository.findAll().forEach(b->log.info("After : "+ b));
    }

    @Test
    public void whenCalledSave_thenCorrectNumberOfUsers() {

        bookRepository.save(Book.builder()
                .name("SomeBook")
                .build()
        );
//        List<Book> users = (List<Book>) bookRepository.findByName("");

        assertThat(bookRepository.findByName("SomeBook")).isPresent();
    }

    @Test
    public void whenLoadSameEntityTwice_thenCompareToEquals() {
        Optional<Book> book1 = bookRepository.findById(1L);
        Optional<Book> book2 = bookRepository.findById(1L);

        log.info("My Book1 " + book1);
        log.info("My Book2 " + book2);
        log.info("My Book1 hash : " + book1.hashCode());
        log.info("My Book2 hash : " + book2.hashCode());
//        log.info("My Book1 " + book1);
        System.out.println(book2);
        assertThat(book1).isEqualTo(book2);
    }

    @Test
    public void whenModifyOldEntity_thenHashCodeShouldRemainSame() {
        Book book = bookRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        int hashCode = book.hashCode();
        book.setName(book.getName()+"modified");
        book = bookRepository.save(book);
        assertThat(book.hashCode()).isEqualTo(hashCode);
    }

    @Test
    public void whenModifyOldEntity_thenVersionShouldIncrement() {
        Book book = bookRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        int version = book.getVersion();
        log.info("My Book Before " + book);
        book.setName(book.getName()+"modified");
        log.info("My Book before persistence " + book);
        book = bookRepository.save(book);
        log.info("My Book After " + book);

        assertThat(book.getVersion()).isEqualTo(version + 1);
    }

    @Test
    @Transactional
    public void whenSearchByAuthorName_thenCorrectBookShouldReturn(){
//        List<BookView> books = bookRepository.findByAuthor_FirstName("Abhishek");
//        assertThat(books.stream()
//                .map(BookView::getAuthor)
//                .map(AuthorView::getFirstName)
//                .collect(Collectors.toList())
//        ).contains("Abhishek");

//        assertThat(books.stream()
//                .map(BookView::getAuthorFirstName)
//                .collect(Collectors.toList())
//        ).contains("Abhishek");
    }

    @Test
    public void whenUsingClassBasedProjections_thenDtoWithRequiredPropertiesIsReturned() {
        List<BookView> books = bookRepository.findByAuthor_FirstName("Abhishek");

        books.forEach(System.out::println);

        assertThat(books).allSatisfy(book -> book.getAuthor().getFirstName().contains("Abhishek"));
    }

}