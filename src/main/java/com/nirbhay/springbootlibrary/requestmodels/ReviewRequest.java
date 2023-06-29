package com.nirbhay.springbootlibrary.requestmodels;

import lombok.Data;

import java.util.Optional;

@Data
public class ReviewRequest {

    private double rating;
    private Long bookId;

    //as its not necessary to take reviewDesc always
    private Optional<String> reviewDescription;


}
