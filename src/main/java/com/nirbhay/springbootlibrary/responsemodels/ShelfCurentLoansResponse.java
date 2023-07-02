package com.nirbhay.springbootlibrary.responsemodels;

import com.nirbhay.springbootlibrary.entity.Book;
import lombok.Data;

@Data
public class ShelfCurentLoansResponse {

    public ShelfCurentLoansResponse(Book book, int daysLeft) {
        this.book = book;
        this.daysLeft = daysLeft;
    }

    private Book book;
    private int daysLeft;


}
