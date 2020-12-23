package com.abhishek.demo.db.repository;

import com.abhishek.demo.db.model.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByName(String name);
    List<Book> findAll(Sort sort);
    Book save(Book book);
}