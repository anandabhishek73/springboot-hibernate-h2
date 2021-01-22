package com.abhishek.demo;

import com.abhishek.demo.db.model.Author;
import com.abhishek.demo.db.model.Book;
import com.abhishek.demo.db.repository.AuthorRepository;
import com.abhishek.demo.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.transaction.Transactional;

//@EntityScan(basePackages = {"com.abhishek.demo.db.model"})
//@EnableJpaRepositories(basePackages = {"com.abhishek.demo.db.repository"})
//@ComponentScan(basePackages = {"com.abhishek.demo"})
//@EnableAutoConfiguration
@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
//        new BeeperControl().beepForAnHour();
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner run(BookService bookService, AuthorRepository authorRepository) throws Exception {
        bookService.createNewBook(Book.builder()
                .name("Fifty shades of grey")
                .ISBN("AB2499DJ00")
                .author(authorRepository.save(Author.builder()
                                .firstName("Michael")
                                .lastName("Jackson")
                                .accountNumber("0123456789")
                                .build()
                        )
                )
                .build()
        );

        bookService.createNewBook(Book.builder()
                .name("Fifty shades of grey - Vol 2")
                .ISBN("AB2499DJ0022")
                .author(authorRepository.save(Author.builder()
                                .firstName("Michael")
                                .lastName("Jackson2")
                                .accountNumber("0123456789")
                                .build()
                        )
                )
                .build()
        );


        bookService.createNewBook(Book.builder().name("Himanshu").build());

        return (String[] args) -> {
            bookService.showAllBooks();
        };
    }


}
