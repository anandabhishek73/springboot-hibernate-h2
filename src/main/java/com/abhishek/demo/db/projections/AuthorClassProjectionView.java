package com.abhishek.demo.db.projections;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * A JPA projection class to only fetch entity attributes that are needed in a flow.
 * This is a class based projection.
 * Entities are the best fit for write operations. Moreover, you should use class-based DTO projections for read operations.
 *
 * https://thorben-janssen.com/spring-data-jpa-query-projections/
 * https://www.baeldung.com/spring-data-jpa-projections
 */
@ToString
public class AuthorClassProjectionView {
    private final String firstName;
    private final String lastName;
    private final Date dob;
    private final String accountNumber;

    public AuthorClassProjectionView(String firstName, String lastName, Date dob, String accountNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.accountNumber = accountNumber;
    }

    public String getFullName(){
        return firstName+ " "+ lastName;
    }
    public int getAge(){
        if (dob==null) return 0;
        return new Date().getYear() - dob.getYear();
    }
    public String getAccountNumber(){
        return accountNumber;
    }
    public String getAccountNumberMasked(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < accountNumber.length(); i++) {
            if(i<2 || i >= accountNumber.length()-2){
                sb.append(accountNumber.charAt(i));
            }else{
                sb.append('*');
            }

        }
        return sb.toString();
    }
}
