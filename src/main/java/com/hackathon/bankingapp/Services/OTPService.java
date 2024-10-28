package com.hackathon.bankingapp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Service
public class OTPService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender mailSender;

    @Autowired
    public OTPService(RedisTemplate<String, String> redisTemplate, JavaMailSender mailSender) {
        this.redisTemplate = redisTemplate;
        this.mailSender = mailSender;
    }

    public String generateOTP(String identifier) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(identifier, otp, Duration.ofMinutes(5));
        return otp;
    }

    public void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("OTP:" + otp);
        mailSender.send(message);
    }

    public boolean verifyOTP(String identifier, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(identifier);
        return storedOtp != null && storedOtp.equals(otp);
    }

    public String generatePasswordResetToken(String identifier) {
        String resetToken = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(identifier + "_reset", resetToken, Duration.ofMinutes(10));
        return resetToken;
    }

    public boolean verifyResetToken(String identifier, String resetToken) {
        String storedToken = redisTemplate.opsForValue().get(identifier + "_reset");
        return storedToken != null && storedToken.equals(resetToken);
    }

}
