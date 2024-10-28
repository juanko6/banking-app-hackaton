package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.DuplicateUserException;
import com.hackathon.bankingapp.Exceptions.InvalidEmailException;
import com.hackathon.bankingapp.Exceptions.InvalidPasswordException;
import com.hackathon.bankingapp.Repositories.UserRepository;
import com.hackathon.bankingapp.Security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;  // Nota: Usamos la interfaz PasswordEncoder
    private final JwtUtil jwtUtil;
    private final LogoutService logoutService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, LogoutService logoutService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.logoutService = logoutService;
    }

    // Métdo para validar contraseñas
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters long");
        }
        if (password.length() > 128) {
            throw new InvalidPasswordException("Password must be less than 128 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new InvalidPasswordException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new InvalidPasswordException("Password must contain at least one digit");
        }
        if (!password.matches(".*[@#$%^&+=].*")) {
            throw new InvalidPasswordException("Password must contain at least one special character");
        }
        if (password.contains(" ")) {
            throw new InvalidPasswordException("Password cannot contain whitespace");
        }
    }

    // Método para validar el formato de correo
    private void validateEmailFormat(String email) {
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new InvalidEmailException("Invalid email: " + email);
        }
    }


    @Transactional
    public User registerUser(User user) {
        // Validación de duplicado
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateUserException("Email already in use");
        }
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new DuplicateUserException("Phone number already in use");
        }

        // Validación de formato de correo y contraseña
        validateEmailFormat(user.getEmail());
        validatePassword(user.getHashedPassword());

        // Registro del usuario
        user.setAccountNumber(UUID.randomUUID().toString());
        user.setHashedPassword(passwordEncoder.encode(user.getHashedPassword()));
        return userRepository.save(user);
    }

    public String loginUser(String identifier, String password) {
        Optional<User> userOpt = userRepository.findByEmailOrAccountNumber(identifier);
        User user = userOpt.orElseThrow(() -> new IllegalStateException("User not found"));

        // Verificar la contraseña
        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new IllegalArgumentException("Incorrect credentials");
        }

        // Generar y retornar el token JWT
        return jwtUtil.generateToken(user.getAccountNumber());
    }

    public User getUserDetails(String accountNumber) {
        return userRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public double getAccountBalance(String accountNumber) {
        User user = getUserDetails(accountNumber);
        return user.getBalance();
    }

    public String extractAccountNumberFromToken(String token) {
        // Quita el prefijo "Bearer " si el token lo tiene
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.extractAccountNumber(token);
    }

    public void logoutUser(String token) {
        long expirationTime = jwtUtil.getExpirationTime(token);
        logoutService.revokeToken(token, expirationTime);
    }

    public String findUserEmail(String identifier) {
        return userRepository.findByEmailOrAccountNumber(identifier)
                .map(User::getEmail)
                .orElse(null);
    }


    public void resetPassword(String identifier, String newPassword) {
        User user = userRepository.findByEmailOrAccountNumber(identifier)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setHashedPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
