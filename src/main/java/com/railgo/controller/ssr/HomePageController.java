package com.railgo.controller.ssr;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.railgo.config.JwtTokenProvider;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/ssr")
public class HomePageController {

    private final JwtTokenProvider jwtTokenProvider;

    public HomePageController(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, Object>> home(
            @RequestHeader(name = "access_token", required = false) String token) {
        Map<String, Object> response = new HashMap<>();

        if (token != null && !token.isBlank() && jwtTokenProvider.validateToken(token)) {
            response.put("tokenIssued", false);
            return ResponseEntity.ok(response);
        }

        String guestToken = jwtTokenProvider.generateGuestToken();
        response.put("tokenIssued", true);
        response.put("access_token", guestToken);
        return ResponseEntity.ok(response);
    }


}
