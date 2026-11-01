package com.railgo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railgo.config.JwtTokenProvider;
import com.railgo.dto.ApiResponse;
import com.railgo.dto.VerifyOtpRequest;
import com.railgo.entity.UserEntity;
import com.railgo.repository.UserRepository;
import com.railgo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class VerifyEmailLoginOtpController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/verify_email_login_otp")
    public ResponseEntity<ApiResponse> verifyLoginOtp(@ModelAttribute VerifyOtpRequest request) {

        if (request.getEmail() == null || request.getEmail().trim().isEmpty()
                || request.getOtp() == null || request.getOtp().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Email and OTP are required!", "MISSING_CREDENTIALS", null));
        }

        try {
            ApiResponse response = emailService.verifyOtp(request);

            // INVALID_OTP, OTP_EXPIRED, NAMES_REQUIRED, ... -> real 4xx
            if (!"success".equals(response.getStatus())) {
                return ResponseEntity.badRequest().body(response);
            }

            // Same email string the service used, so the lookup always matches
            UserEntity user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new IllegalStateException("User missing after verification"));

            // Enforce single-device session: generate and persist a new session ID
            String newSessionId = UUID.randomUUID().toString();
            user.setCurrentSessionId(newSessionId);
            userRepository.save(user);

            // Issue JWT token with sessionId claim
            String token = jwtTokenProvider.generateUserToken(user.getUserId(), newSessionId);
            String userJson = buildIsUserHeader(user);

            HttpHeaders headers = new HttpHeaders();
            headers.set("token", token);
            headers.set("access_token", token);
            headers.set("isUser", userJson);
            headers.set("isuser", userJson);
            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "token, access_token, isUser, isuser");

            return ResponseEntity.ok().headers(headers).body(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse(false, "Unexpected error occurred during verification", "UNEXPECTED_ERROR_OCCURRED", null));
        }
    }

    // Keep identical to the isUser header in HomePageController
    private String buildIsUserHeader(UserEntity user) throws Exception {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("user_id", user.getUserId());
        details.put("first_name", user.getFirstName());
        details.put("last_name", user.getLastName());
        details.put("email", user.getEmail());
        details.put("phone", user.getPhoneNumber());
        return objectMapper.writeValueAsString(details);
    }
}
