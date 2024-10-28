package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByAccountNumber(String accountNumber);

    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.accountNumber = :identifier")
    Optional<User> findByEmailOrAccountNumber(@Param("identifier") String identifier);

}
