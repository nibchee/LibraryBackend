package com.nirbhay.springbootlibrary.service;

import com.nirbhay.springbootlibrary.dao.BookRepository;
import com.nirbhay.springbootlibrary.dao.CheckoutRepository;
import com.nirbhay.springbootlibrary.entity.Book;
import com.nirbhay.springbootlibrary.entity.Checkout;
import com.nirbhay.springbootlibrary.responsemodels.ShelfCurentLoansResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/*
Book Service functionality is:
1.Book CheckOut functionlity
When user demands checkOuts for a specific book, then this Service
validates presence of book , does same book already checkout & user trying to checkout again &no book copies availble
So on passing validation, it decrements the book count & saves checkout
 2.Book LoanCount functionality
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


        Checkout checkout = new Checkout(userEmail, LocalDate.now().toString(), LocalDate.now().plusDays(7).toString(), book.get().getId());
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

    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }


    public List<ShelfCurentLoansResponse> currentLoans(String userEmail) throws Exception {

        List<ShelfCurentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();

        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);
        List<Long> bookIdList = new ArrayList<>();

        for (Checkout i : checkoutList) {
            bookIdList.add(i.getBookId());
        }

        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Book book : books) {
            Optional<Checkout> checkout = checkoutList.stream()
                    .filter(x -> x.getBookId() == book.getId()).findFirst();

            if (checkout.isPresent()) {

                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());

                TimeUnit time = TimeUnit.DAYS;

                long difference_In_Time = time.convert(d1.getTime() - d2.getTime(),
                        TimeUnit.MILLISECONDS);

                shelfCurrentLoansResponses.add(new ShelfCurentLoansResponse(book, (int) difference_In_Time));
            }
        }
        return shelfCurrentLoansResponses;
    }

}
