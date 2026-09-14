package com.railgo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String generateAndSendOtp(String toEmail) {
        Random random = new Random();
        int otpValue = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpValue);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("RailGo - Your Login OTP");
        message.setText("Welcome to RailGo! Your One Time Password (OTP) for login is: " + otp + "\n\nPlease do not share this code with anyone.");
        mailSender.send(message);
        return otp;
    }

}

