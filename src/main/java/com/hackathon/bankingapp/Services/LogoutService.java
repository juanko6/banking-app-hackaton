package com.hackathon.bankingapp.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LogoutService {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public LogoutService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void revokeToken(String token, long expirationTime) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(token, "revoked", Duration.ofMillis(expirationTime));
        }
    }

    public boolean isTokenRevoked(String token) {
        if (redisTemplate != null && token != null) {
            Boolean result = redisTemplate.hasKey(token);
            return result != null && result;
        }
        return false;
    }
}
