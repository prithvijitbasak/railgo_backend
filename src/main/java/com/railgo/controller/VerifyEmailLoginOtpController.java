package com.railgo.controller;

import com.railgo.dto.ApiResponse;
import com.railgo.dto.VerifyOtpRequest;
import com.railgo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class VerifyEmailLoginOtpController {

    @Autowired
    private EmailService emailService;

    // Replaced @RequestBody with @ModelAttribute to accept form-data
    @PostMapping("/verify_email_login_otp")
    public ResponseEntity<ApiResponse> verifyLoginOtp(@ModelAttribute VerifyOtpRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty() || request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email and OTP are required!", "MISSING_CREDENTIALS", null));
        }

        try {
            ApiResponse response = emailService.verifyOtp(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Unexpected error occurred during verification", "UNEXPECTED_ERROR_OCCURRED", null));
        }
    }
}