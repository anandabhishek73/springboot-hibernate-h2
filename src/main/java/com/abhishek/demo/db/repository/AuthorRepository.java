package com.abhishek.demo.db.repository;

import com.abhishek.demo.db.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Dynamic Projections:
     * Depending on the class you provide when you call the repository method, Spring Data JPA uses one of the
     * previously described mechanisms to define the projection and map it. For example, if you provide a
     * DTO class, Spring Data JPA generates a query with a constructor expression. Your persistence provider
     * then selects the required database columns and returns a DTO object.
     * @param firstName - query param
     * @param type<T> - The Class type of projection to which the returned data will be mapped by persistence provider.
     * @return List of projected objects from DB rows.
     */
    <T> List<T> findAllByFirstName(String firstName, Class<T> type);
    <T> List<T> findAllByAccountNumber(String accountNumber, Class<T> type);
}
