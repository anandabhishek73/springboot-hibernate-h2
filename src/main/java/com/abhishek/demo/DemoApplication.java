package com.abhishek.demo;

import com.abhishek.demo.db.model.Book;
import com.abhishek.demo.db.repository.BookRepository;
import com.abhishek.demo.service.BookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.TypedSort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

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
    public CommandLineRunner run(BookService bookService) throws Exception {
        return (String[] args) -> {
            bookService.showAllBooks();
        };
    }


}
