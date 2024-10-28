package com.hackathon.bankingapp.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Data
@AllArgsConstructor
@Entity
@Table(name = "transactions")
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private Date transactionDate;

    @Column(nullable = false)
    private String sourceAccountNumber;

    @Column
    private String targetAccountNumber;

    public enum TransactionType {
        CASH_DEPOSIT,
        CASH_WITHDRAWAL,
        CASH_TRANSFER,
        SUBSCRIPTION,
        ASSET_PURCHASE,
        ASSET_SELL
    }
}
