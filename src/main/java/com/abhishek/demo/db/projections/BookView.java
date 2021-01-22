package com.abhishek.demo.db.projections;

import com.abhishek.demo.db.model.Author;


public interface BookView {
    String getName();
    String getISBN();
    AuthorView getAuthor();
//    AuthorClassProjectionView getAuthor();
//    String getAuthorFirstName();
//    String getAuthorLastName();
}
