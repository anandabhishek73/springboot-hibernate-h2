package com.abhishek.demo.db.repository;

import com.abhishek.demo.db.projections.AuthorClassProjectionView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void getAllAuthorsByFirstName_thenTestDynamicProjectionMapping() throws JsonProcessingException {
        List<AuthorClassProjectionView> authors = authorRepository.findAllByFirstName("Abhishek", AuthorClassProjectionView.class);
        authors.forEach(System.out::println);
        ObjectMapper mapper = new ObjectMapper();
        for (AuthorClassProjectionView author : authors) {
            System.out.println(mapper.writeValueAsString(author));
        }

        Condition<AuthorClassProjectionView> havingFirstNameAsAbhishek = new Condition<AuthorClassProjectionView>(a -> a.getFullName().contains("Abhishek"), "having First Name As Abhishek");
        assertThat(authors).are(havingFirstNameAsAbhishek);
    }
    @Test
    void findAllByAccountNumber_givesCorrectResponse_whenSearchedOverEncryptedAttribute() throws JsonProcessingException {
        List<AuthorClassProjectionView> authors = authorRepository.findAllByAccountNumber("0123456789", AuthorClassProjectionView.class);
        authors.forEach(System.out::println);
        ObjectMapper mapper = new ObjectMapper();
        for (AuthorClassProjectionView author : authors) {
            System.out.println(mapper.writeValueAsString(author));
        }

        Condition<AuthorClassProjectionView> havingAccountNumberAs0123456789 = new Condition<AuthorClassProjectionView>(a -> a.getAccountNumber().contains("0123456789"), "having account number as 0123456789");
        assertThat(authors).are(havingAccountNumberAs0123456789);
    }
}