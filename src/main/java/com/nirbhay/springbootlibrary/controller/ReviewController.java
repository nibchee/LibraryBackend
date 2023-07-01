package com.nirbhay.springbootlibrary.controller;

import com.nirbhay.springbootlibrary.requestmodels.ReviewRequest;
import com.nirbhay.springbootlibrary.service.ReviewServie;
import com.nirbhay.springbootlibrary.utils.ExtractJWT;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private ReviewServie reviewServie;

    public ReviewController(ReviewServie reviewServie) {
        this.reviewServie = reviewServie;
    }

    @GetMapping("/secure/user/book")
    public Boolean reviewBookByUser(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (userEmail == null) {
            throw new Exception("userEMail is missing");
        }
        return reviewServie.userReviewListed(userEmail, bookId);
    }

    @PostMapping("/secure")
    public void postReview(@RequestHeader(value = "Authorization") String token, @RequestBody ReviewRequest reviewRequest) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if (userEmail == null) {
            throw new Exception("   UserEmail is missing");
        }

        reviewServie.postReview(userEmail, reviewRequest);
    }

}
