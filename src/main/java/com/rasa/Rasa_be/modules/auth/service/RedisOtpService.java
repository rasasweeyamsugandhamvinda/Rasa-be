package com.rasa.Rasa_be.modules.auth.service;


public interface RedisOtpService {
    public void storeOtp(String email, String rawOtp);
    public boolean verifyOtp(String email, String rawOtp);
}
