package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.Entities.Transaction;
import com.hackathon.bankingapp.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<Map<String, String>> deposit(@RequestBody Map<String, Object> request, Principal principal) {
        String pin = (String) request.get("pin");
        double amount = (double) request.get("amount");
        String message = transactionService.deposit(principal.getName(), pin, amount);
        return ResponseEntity.ok(Map.of("msg", message));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(@RequestBody Map<String, Object> request, Principal principal) {
        String pin = (String) request.get("pin");
        double amount = (double) request.get("amount");
        String message = transactionService.withdraw(principal.getName(), pin, amount);
        return ResponseEntity.ok(Map.of("msg", message));
    }

    @PostMapping("/fund-transfer")
    public ResponseEntity<Map<String, String>> transfer(@RequestBody Map<String, Object> request, Principal principal) {
        String pin = (String) request.get("pin");
        double amount = (double) request.get("amount");
        String targetAccountNumber = (String) request.get("targetAccountNumber");
        String message = transactionService.transfer(principal.getName(), pin, amount, targetAccountNumber);
        return ResponseEntity.ok(Map.of("msg", message));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getTransactionHistory(Principal principal) {
        List<Transaction> transactions = transactionService.getTransactionHistory(principal.getName());
        return ResponseEntity.ok(transactions);
    }
}
