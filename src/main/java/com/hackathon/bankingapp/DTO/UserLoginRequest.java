package com.hackathon.bankingapp.DTO;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String identifier;  // Puede ser el email o el número de cuenta
    private String password;
}
