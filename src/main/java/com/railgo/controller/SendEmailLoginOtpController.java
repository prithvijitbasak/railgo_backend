package com.railgo.controller;

import com.railgo.service.EmailService;
import com.railgo.repository.UserRepository;
import com.railgo.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SendEmailLoginOtpController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send_email_login_otp")
    public ResponseEntity<ApiResponse> sendLoginOtp(@RequestParam("email") String email) {
        if(email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email is required!", "EMAIL_IS_REQUIRED", null));
        }

        try {
            emailService.generateAndSendOtp(email);

            boolean userExists = userRepository.existsByEmail(email);

            if (userExists) {
                return ResponseEntity.ok(new ApiResponse(true, "OTP sent to existing user.", "OTP_SENT_EXISTING_USER", "login"));
            } else {
                return ResponseEntity.ok(new ApiResponse(true, "OTP sent to new user.", "OTP_SENT_NEW_USER", "registration"));
            }

        } catch(Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Unexpected error occurred", "UNEXPECTED_ERROR_OCCURRED", null));
        }
    }
}