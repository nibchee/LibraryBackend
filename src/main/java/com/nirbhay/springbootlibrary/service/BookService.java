package com.nirbhay.springbootlibrary.service;

import com.nirbhay.springbootlibrary.dao.BookRepository;
import com.nirbhay.springbootlibrary.dao.CheckoutRepository;
import com.nirbhay.springbootlibrary.dao.HistoryRepository;
import com.nirbhay.springbootlibrary.dao.PaymentRepository;
import com.nirbhay.springbootlibrary.entity.Book;
import com.nirbhay.springbootlibrary.entity.Checkout;
import com.nirbhay.springbootlibrary.entity.History;
import com.nirbhay.springbootlibrary.entity.Payment;
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

    private HistoryRepository historyRepository;

    private PaymentRepository paymentRepository;

    //here we are using constructor dependency injection
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository, HistoryRepository historyRepository, PaymentRepository paymentRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
        this.paymentRepository = paymentRepository;
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

        List<Checkout> currentBooksCheckOut = checkoutRepository.findBooksByUserEmail(userEmail);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        boolean bookNeedsReturned = false;

        for (Checkout checkout : currentBooksCheckOut) {
            Date d1 = sdf.parse(checkout.getReturnDate());
            Date d2 = sdf.parse(LocalDate.now().toString());
            TimeUnit time = TimeUnit.DAYS;
            double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);
            if (differenceInTime < 0) {
                bookNeedsReturned = true;
                break;
            }
        }

        Payment userPayment = paymentRepository.findByUserEmail(userEmail);

        if ((userPayment != null && userPayment.getAmount() > 0) || (userPayment != null && bookNeedsReturned)) {
            throw new Exception("Outstanding fees");
        }

        if (userPayment == null) {
            Payment payment = new Payment();
            payment.setAmount(00.00);
            payment.setUserEmail(userEmail);
            paymentRepository.save(payment);
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

    public void returnBook(String userEmail, Long bookId) throws Exception {
        Optional<Book> book = bookRepository.findById(bookId);

        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if (!book.isPresent() || validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by User");
        }

        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        bookRepository.save(book.get());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdf.parse(validateCheckout.getReturnDate());
        Date d2 = sdf.parse(LocalDate.now().toString());
        TimeUnit time = TimeUnit.DAYS;
        double differenceinTime = time.convert(d1.getTime() - d1.getTime(), TimeUnit.MILLISECONDS);

        if (differenceinTime < 0) {
            Payment payment = paymentRepository.findByUserEmail(userEmail);
            payment.setAmount(payment.getAmount() + (differenceinTime * -1));
            paymentRepository.save(payment);
        }

        checkoutRepository.deleteById(validateCheckout.getId());

        //after returning book saving in history
        History history = new History(
                userEmail,
                validateCheckout.getCheckoutDate(),
                LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImg()
        );

        historyRepository.save(history);

    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if (validateCheckout == null) {
            throw new Exception("Book does not exist or not checked out by the user");
        }

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = sdFormat.parse(validateCheckout.getReturnDate());
        Date d2 = sdFormat.parse(LocalDate.now().toString());

        if (d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString());
            checkoutRepository.save(validateCheckout);
        }
    }
}
