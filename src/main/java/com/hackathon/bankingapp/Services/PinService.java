package com.hackathon.bankingapp.Services;

import com.hackathon.bankingapp.Entities.User;
import com.hackathon.bankingapp.Exceptions.InvalidPinException;
import com.hackathon.bankingapp.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PinService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PinService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Crear un PIN nuevo
    public String createPin(String identifier, String password, String pin) {
        User user = userRepository.findByEmailOrAccountNumber(identifier)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new IllegalArgumentException("Bad credentials");
        }

        user.setPin(pin);
        userRepository.save(user);
        return "PIN created successfully";
    }

    // Actualizar un PIN existente
    public String updatePin(String identifier, String oldPin, String password, String newPin) {
        User user = userRepository.findByEmailOrAccountNumber(identifier)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!passwordEncoder.matches(password, user.getHashedPassword())) {
            throw new IllegalArgumentException("Bad credentials");
        }

        if (user.getPin() == null || !user.getPin().equals(oldPin)) {
            throw new IllegalArgumentException("Incorrect old PIN");
        }

        user.setPin(newPin);
        userRepository.save(user);
        return "PIN updated successfully";
    }

    public void verifyPin(User user, String pin) {
        if (user.getPin() == null || !passwordEncoder.matches(pin, user.getPin())) {
            throw new InvalidPinException("Invalid PIN");
        }
    }
}
