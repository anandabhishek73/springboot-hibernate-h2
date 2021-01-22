package com.abhishek.demo.db.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


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

    private String accountNumber;

//    @Transient
//    private transient Date age;
//
//    public Date getAge() {
//        return new Date(new Date().compareTo(age));
//    }

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    Set<Book> books;
}
