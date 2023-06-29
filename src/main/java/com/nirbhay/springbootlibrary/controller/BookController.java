package com.nirbhay.springbootlibrary.controller;

import com.nirbhay.springbootlibrary.entity.Book;
import com.nirbhay.springbootlibrary.service.BookService;
import com.nirbhay.springbootlibrary.utils.ExtractJWT;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"email\"");
        return bookService.currentLoansCount(userEmail);
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"email\"");
        return bookService.checkoutBook(userEmail, bookId);
    }

    //IsBook checkout By User
    @GetMapping("/secure/ischeckout/byuser")
    public Boolean checkoutBookByUser(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"email\"");
        return bookService.checkoutBookByUser(userEmail, bookId);
    }
}
