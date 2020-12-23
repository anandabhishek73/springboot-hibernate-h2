package com.abhishek.demo.service;

import com.abhishek.demo.db.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(SpringRunner.class)
@SpringBootTest
class BookServiceTest {

    @Autowired
    private BookService bookService;



    @Test
    public void whenApplicationStarts_thenHibernateCreatesInitialRecords() {
        List<Book> books = bookService.list();

        assertThat(books.size()).isEqualTo(3);
    }

}