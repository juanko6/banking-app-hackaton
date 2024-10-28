package com.hackathon.bankingapp.Controllers;

import com.hackathon.bankingapp.DTO.*;
import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Services.OTPService;
import com.hackathon.bankingapp.Services.PinService;
import com.hackathon.bankingapp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final OTPService otpService;
    private final PinService pinService;

    @Autowired
    public UserController(PinService pinService, OTPService otpService, UserService userService) {
        this.pinService = pinService;
        this.otpService = otpService;
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        try {
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAddress(request.getAddress());
            user.setHashedPassword(request.getPassword());

            User registeredUser = userService.registerUser(user);

            return ResponseEntity.ok(new UserResponseDTO(registeredUser));

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginRequest request) {
        try {
            String token = userService.loginUser(request.getIdentifier(), request.getPassword());
            return ResponseEntity.ok(new TokenResponse(token));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }
    }

    @GetMapping("/dashboard/user")
    public ResponseEntity<UserResponseDTO> getUserDetails(@RequestHeader("Authorization") String token) {
        String accountNumber = userService.extractAccountNumberFromToken(token);
        User userDetails = userService.getUserDetails(accountNumber);
        return ResponseEntity.ok(new UserResponseDTO(userDetails));
    }

    @GetMapping("/dashboard/account")
    public ResponseEntity<AccountBalanceDTO> getAccountBalance(@RequestHeader("Authorization") String token) {
        String accountNumber = userService.extractAccountNumberFromToken(token);
        double balance = userService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(new AccountBalanceDTO(accountNumber, balance));
    }

    @PostMapping("/users/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        // Quitar el prefijo "Bearer " si está presente
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        userService.logoutUser(token);
        return ResponseEntity.ok("User logged out successfully");
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Application is running say juanko.dev", HttpStatus.OK);
    }


    // 1. Enviar OTP
    @PostMapping("/auth/password-reset/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String email = userService.findUserEmail(identifier);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "User not found"));
        }
        String otp = otpService.generateOTP(identifier);
        otpService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Collections.singletonMap("message", "OTP sent successfully to: " + email));
    }

    // 2. Verificar OTP
    @PostMapping("/auth/password-reset/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String otp = request.get("otp");

        if (otpService.verifyOTP(identifier, otp)) {
            String resetToken = otpService.generatePasswordResetToken(identifier);
            return ResponseEntity.ok(Collections.singletonMap("passwordResetToken", resetToken));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid OTP"));
        }
    }

    // 3. Restablecer contraseña
    @PostMapping("/auth/password-reset")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String resetToken = request.get("resetToken");
        String newPassword = request.get("newPassword");

        if (otpService.verifyResetToken(identifier, resetToken)) {
            userService.resetPassword(identifier, newPassword);
            return ResponseEntity.ok(Collections.singletonMap("message", "Password reset successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid reset token"));
        }
    }

    // Endpoint para crear un PIN
    @PostMapping("/account/pin/create")
    public ResponseEntity<Map<String, String>> createPin(@RequestBody Map<String, String> request, Principal principal) {
        String identifier = principal.getName();
        String password = request.get("password");
        String pin = request.get("pin");

        String message = pinService.createPin(identifier, password, pin);
        return ResponseEntity.ok(Collections.singletonMap("msg", message));
    }

    // Endpoint para actualizar el PIN
    @PostMapping("/account/pin/update")
    public ResponseEntity<Map<String, String>> updatePin(@RequestBody Map<String, String> request, Principal principal) {
        String identifier = principal.getName();
        String oldPin = request.get("oldPin");
        String password = request.get("password");
        String newPin = request.get("newPin");

        String message = pinService.updatePin(identifier, oldPin, password, newPin);
        return ResponseEntity.ok(Collections.singletonMap("msg", message));
    }

}
