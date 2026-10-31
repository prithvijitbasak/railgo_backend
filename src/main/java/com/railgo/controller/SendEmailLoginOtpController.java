package com.railgo.controller;

import com.railgo.config.JwtTokenProvider;
import com.railgo.dto.ApiResponse;
import com.railgo.repository.UserRepository;
import com.railgo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SendEmailLoginOtpController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/send_email_login_otp")
    public ResponseEntity<ApiResponse> sendLoginOtp(
            @RequestParam("email") String email,
            @RequestHeader(name = "access_token", required = false) String token) {

        // Valid token sent -> don't issue a new one. Otherwise issue a guest token.
        HttpHeaders headers = new HttpHeaders();
        boolean valid = token != null && !token.isBlank() && jwtTokenProvider.validateToken(token);
        try {
            valid = token != null && !token.isBlank() && jwtTokenProvider.validateToken(token);
        } catch (Exception e) {
            valid = false;   // expired / malformed / bad signature -> treat as no token
        }
        if (!valid) {
            headers.set("token", jwtTokenProvider.generateGuestToken());
            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "token");
        }

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .headers(headers)
                    .body(new ApiResponse(false, "Email is required!", "EMAIL_IS_REQUIRED", null));
        }

        try {
            emailService.generateAndSendOtp(email);

            boolean userExists = userRepository.existsByEmail(email);

            if (userExists) {
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new ApiResponse(true, "OTP sent to existing user.", "OTP_SENT_EXISTING_USER", "login"));
            } else {
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(new ApiResponse(true, "OTP sent to new user.", "OTP_SENT_NEW_USER", "registration"));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .headers(headers)
                    .body(new ApiResponse(false, "Unexpected error occurred", "UNEXPECTED_ERROR_OCCURRED", null));
        }
    }
}