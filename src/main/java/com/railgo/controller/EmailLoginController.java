package com.railgo.controller;

import com.railgo.service.EmailService;
import com.railgo.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class EmailLoginController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send_email_login_otp")
    public ResponseEntity<ApiResponse> sendLoginOtp(@RequestParam("email") String email) {
        if(email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email is required!"));
        }

        try {
            String generatedOtp= emailService.generateAndSendOtp(email);
            return ResponseEntity.ok(new ApiResponse(true, "OTP successfully sent to: " + email));
        }
        catch(Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse(false, "Failed to send OTP: " + e.getMessage()));
        }
    }
}
