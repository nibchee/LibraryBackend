package com.nirbhay.springbootlibrary.service;

import com.nirbhay.springbootlibrary.dao.BookRepository;
import com.nirbhay.springbootlibrary.dao.CheckoutRepository;
import com.nirbhay.springbootlibrary.entity.Book;
import com.nirbhay.springbootlibrary.entity.Checkout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/*
Book Service functionality is: When user demands checkOuts for a specific book, then this Service
validates presence of book , does same book already checkout & user trying to checkout again &no book copies availble
So on passing validation, it decrements the book count & saves checkout
 */
@Service
@Transactional
public class BookService {
    private BookRepository bookRepository;

    private CheckoutRepository checkoutRepository;

    //here we are using constructor dependency injection
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
    }

    public Book checkoutBook(String userEmail, Long bookId) throws Exception {
        //On chekout verifying does book user wants to checkout is present or not
        Optional<Book> book = bookRepository.findById(bookId);

        //checkout object for which user wants to check
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        //if book not present|| chekout is laready done for same book|| copes are 0 then throws exception
        if (!book.isPresent() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book doesn't exist or already checked out by user");
        }

        //book.get() is an Optional Function & oncehout we decrement available copies
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        bookRepository.save(book.get());


        Checkout checkout = new Checkout(
                userEmail,
                LocalDate.now().toString(),
                LocalDate.now().plusDays(7).toString(),
                book.get().getId()
        );
        //saving checkout
        checkoutRepository.save(checkout);
        return book.get();
    }

    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout != null) {
            return true;
        }
        return false;
    }
}
