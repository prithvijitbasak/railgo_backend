package com.railgo.service;

import com.railgo.dto.ApiResponse;
import com.railgo.dto.VerifyOtpRequest;
import com.railgo.entity.OtpEntity;
import com.railgo.entity.UserEntity;
import com.railgo.repository.OtpRepository;
import com.railgo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generateAndSendOtp(String email) {
        String otp = String.format("%06d", secureRandom.nextInt(1000000));

        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setCreat(Instant.now());
        otpEntity.setExp(Instant.now().plus(5, ChronoUnit.MINUTES));
        otpEntity.setUsed((short) 0);

        otpRepository.save(otpEntity);
        sendOtpViaEmail(email, otp);

        return otp;
    }

    private void sendOtpViaEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("RailGo - Your Login OTP");
        message.setText("Welcome to RailGo! Your One Time Password (OTP) for login is: " + otp + "\n\nPlease do not share this code with anyone.");
        mailSender.send(message);
    }

    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        Optional<OtpEntity> otpOpt = otpRepository.findTopByEmailOrderByCreatDesc(request.getEmail());

        if (otpOpt.isEmpty() || !otpOpt.get().getOtp().equals(request.getOtp())) {
            return new ApiResponse(false, "Invalid OTP.", "INVALID_OTP", null);
        }

        OtpEntity otpEntity = otpOpt.get();

        if (otpEntity.getUsed() == 1) {
            return new ApiResponse(false, "This OTP has already been used.", "OTP_ALREADY_USED", null);
        }
        if (otpEntity.getExp().isBefore(Instant.now())) {
            return new ApiResponse(false, "This OTP has expired.", "OTP_EXPIRED", null);
        }

        boolean userExists = userRepository.existsByEmail(request.getEmail());

        if (!userExists) {
            if (request.getFirstName() == null || request.getFirstName().trim().isEmpty() ||
                    request.getLastName() == null || request.getLastName().trim().isEmpty()) {
                return new ApiResponse(false, "First and last name are required for new accounts.", "NAMES_REQUIRED", null);
            }

            UserEntity newUser = new UserEntity();
            newUser.setEmail(request.getEmail());
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());

            // Set the creation timestamp
            newUser.setCreatedAt(LocalDateTime.now()); // Or LocalDateTime.now() depending on your UserEntity setup

            // If phone_number is strictly required by your DB but not collected in the form,
            // you must provide a default value to prevent crashes, or alter your DB to allow nulls.
            newUser.setPhoneNumber("");

            userRepository.save(newUser);
        }

        otpEntity.setUsed((short) 1);
        otpRepository.save(otpEntity);

        if (userExists) {
            return new ApiResponse(true, "Login successful.", "LOGIN_SUCCESS", null);
        } else {
            return new ApiResponse(true, "Account created and logged in successfully.", "SIGNUP_SUCCESS", null);
        }
    }
}