package com.abhishek.demo.db.repository;

import com.abhishek.demo.db.projections.BookView;
import com.abhishek.demo.db.model.Book;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByName(String name);
    Streamable<Book> findByNameContaining(String name);
//    List<BookWithAuthor> findBooks(Sort sort);
    List<BookView> findByAuthor_FirstName(@NonNull String firstName);

    List<BookView> findAllByIdGreaterThan(Long id);

//    List<AuthorClassProjectionView> findByAuthor_LastName(@NonNull String lastName);

    //    List<Book> findByAuthor_FirstName(@NonNull String firstName);
//    <T> List<T> findAllAndSort(Sort sort, Class<T> projectionType);
//    <T> List<T> findAll(Class<T> projectionType);
    Book save(Book book);

}