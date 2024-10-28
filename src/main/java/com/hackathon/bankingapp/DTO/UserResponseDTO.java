package com.hackathon.bankingapp.DTO;

import com.hackathon.bankingapp.Entities.User;
import lombok.Data;

@Data
public class UserResponseDTO {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String accountNumber;
    private String hashedPassword;

    public UserResponseDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.accountNumber = user.getAccountNumber();
        this.hashedPassword = user.getHashedPassword();
    }
}
