package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountNumber(String accountNumber);
}
