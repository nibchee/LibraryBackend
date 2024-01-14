package com.nirbhay.springbootlibrary.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "payment")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_Email")
    private String userEmail;

    @Column(name = "amount")
    private double amount;
}
