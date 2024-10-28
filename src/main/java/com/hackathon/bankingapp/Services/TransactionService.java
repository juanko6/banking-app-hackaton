package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Repositories.TransactionRepository;
import com.hackathon.bankingapp.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PinService pinService;

    @Autowired
    public TransactionService(UserRepository userRepository, TransactionRepository transactionRepository, PinService pinService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.pinService = pinService;
    }

    @Transactional
    public String deposit(String identifier, String pin, double amount) {
        User user = userRepository.findByEmailOrAccountNumber(identifier)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        pinService.verifyPin(user, pin);

        user.setBalance(user.getBalance() + amount);
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.CASH_DEPOSIT);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccountNumber(user.getAccountNumber());
        transaction.setTargetAccountNumber("N/A");
        transactionRepository.save(transaction);

        return "Cash deposited successfully";
    }

    @Transactional
    public String withdraw(String identifier, String pin, double amount) {
        User user = userRepository.findByEmailOrAccountNumber(identifier)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        pinService.verifyPin(user, pin);

        if (user.getBalance() < amount) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.CASH_WITHDRAWAL);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccountNumber(user.getAccountNumber());
        transaction.setTargetAccountNumber("N/A");
        transactionRepository.save(transaction);

        return "Cash withdrawn successfully";
    }

    @Transactional
    public String transfer(String identifier, String pin, double amount, String targetAccountNumber) {
        User sourceUser = userRepository.findByEmailOrAccountNumber(identifier)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        User targetUser = userRepository.findByAccountNumber(targetAccountNumber)
                .orElseThrow(() -> new IllegalStateException("Target account not found"));

        pinService.verifyPin(sourceUser, pin);

        if (sourceUser.getBalance() < amount) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        sourceUser.setBalance(sourceUser.getBalance() - amount);
        targetUser.setBalance(targetUser.getBalance() + amount);

        userRepository.save(sourceUser);
        userRepository.save(targetUser);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(Transaction.TransactionType.CASH_TRANSFER);
        transaction.setTransactionDate(new Date());
        transaction.setSourceAccountNumber(sourceUser.getAccountNumber());
        transaction.setTargetAccountNumber(targetAccountNumber);
        transactionRepository.save(transaction);

        return "Fund transferred successfully";
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findBySourceAccountNumber(accountNumber);
    }

}
