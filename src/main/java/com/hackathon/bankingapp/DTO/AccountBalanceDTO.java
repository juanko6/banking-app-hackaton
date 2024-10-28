package com.hackathon.bankingapp.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountBalanceDTO {
    private String accountNumber;
    private double balance;
}
