package com.hackathon.bankingapp.DTO;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String password;
}
