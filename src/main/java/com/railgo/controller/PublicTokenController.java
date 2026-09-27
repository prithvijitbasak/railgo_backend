package com.railgo.controller;

import com.railgo.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class PublicTokenController {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/guest-token")
    public ResponseEntity<Map<String, String>> getGuestToken() {
        // Call the provider to generate the real JWT string
        String token = jwtTokenProvider.generateGuestToken();

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}