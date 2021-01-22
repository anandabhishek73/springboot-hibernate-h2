package com.abhishek.demo.db.model;

import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@Builder
@Entity
@Table(name = "book",
        indexes = {
                @Index(columnList = "name", name = "name_idx")
        })
public class Book extends BaseEntity {

    private String name;

    @NaturalId()
    private String ISBN;

    //    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Author author;

    // standard constructors

    // standard getters and setters

//    @Override
//    public String toString() {
//        return "Book{" + super.toString() +
//                ", name='" + name + '\'' +
//                ", ISBN='" + ISBN + '\'' +
////                ", author=" + author +
//                '}';
//    }
}
