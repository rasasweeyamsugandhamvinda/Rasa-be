package com.rasa.Rasa_be.modules.auth.service.impl;

import com.rasa.Rasa_be.modules.auth.service.JwtTokenService;
import com.rasa.Rasa_be.modules.auth.service.RedisOtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisOtpServiceImpl implements RedisOtpService {

    private final StringRedisTemplate redisTemplate;
    private final JwtTokenService jwtTokenService;

    private static final String OTP_PREFIX = "auth:otp:";
    private static final Duration OTP_EXPIRATION = Duration.ofMinutes(5);

    public void storeOtp(String email, String rawOtp) {
        String key = OTP_PREFIX + email.toLowerCase();
        String hashedOtp = jwtTokenService.hashToken(rawOtp);
        redisTemplate.opsForValue().set(key, hashedOtp, OTP_EXPIRATION);
        log.info("Stored hashed OTP in Redis for email: {}", email);
    }

    public boolean verifyOtp(String email, String rawOtp) {
        String key = OTP_PREFIX + email.toLowerCase();
        String storedHash = redisTemplate.opsForValue().get(key);

        if (storedHash == null) {
            log.warn("OTP verification failed. OTP expired or not found for email: {}", email);
            return false;
        }

        String inputHash = jwtTokenService.hashToken(rawOtp);
        boolean isValid = storedHash.equals(inputHash);

        if (isValid) {
            redisTemplate.delete(key);
            log.info("OTP verified successfully and removed from Redis for email: {}", email);
        } else {
            log.warn("Invalid OTP attempt for email: {}", email);
        }

        return isValid;
    }

}
