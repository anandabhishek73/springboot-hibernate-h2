package com.abhishek.demo.db.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity(name = "Author")
@Table(name = "author")
public class Author extends BaseEntity {

    private String firstName;
    private String lastName;

    @Column(name = "dob")
    private Date dob;

//    @Transient
//    private transient Date age;
//
//    public Date getAge() {
//        return new Date(new Date().compareTo(age));
//    }

//    @OneToMany(mappedBy = "author")
//    Set<Book> books;
}
