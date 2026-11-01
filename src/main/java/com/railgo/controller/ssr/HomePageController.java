package com.railgo.controller.ssr;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railgo.config.JwtTokenProvider;
import com.railgo.dto.UserDetailsDto;
import com.railgo.entity.UserEntity;
import com.railgo.service.UserService;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/ssr")
public class HomePageController {

    private static final Logger log = LoggerFactory.getLogger(HomePageController.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HomePageController(JwtTokenProvider jwtTokenProvider,
                              UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @GetMapping("/home")
    public ResponseEntity<Map<String, String>> home(
            @RequestHeader(name = "token", required = false) String tokenHeader,
            @RequestHeader(name = "access_token", required = false) String accessTokenHeader,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        try {
            // Resolve token from any supported header format
            String rawToken = tokenHeader != null ? tokenHeader
                    : (accessTokenHeader != null ? accessTokenHeader : authHeader);

            // Strip "Bearer " prefix if present
            String token = (rawToken != null && rawToken.startsWith("Bearer "))
                    ? rawToken.substring(7).trim()
                    : (rawToken != null ? rawToken.trim() : null);

            log.info("Incoming /home request - raw token present: {}, stripped token present: {}", 
                    rawToken != null, token != null);

            String newToken = null;      // set only when we generate a new guest token
            String isUser = "false";     // Default fallback string
            boolean forcedLogout = false;

            boolean valid = token != null && !token.isBlank() && jwtTokenProvider.validateToken(token);

            if (valid) {
                try {
                    Long userId = jwtTokenProvider.getUserId(token);   // throws for guest tokens
                    String tokenSessionId = jwtTokenProvider.getSessionId(token);

                    // Check single-device session validity against database
                    Optional<UserEntity> userOpt = userService.findById(userId);

                    if (userOpt.isPresent()) {
                        UserEntity user = userOpt.get();
                        if (tokenSessionId != null && !tokenSessionId.isBlank() && tokenSessionId.equals(user.getCurrentSessionId())) {
                            isUser = buildIsUserHeader(UserDetailsDto.from(user));
                            log.info("Valid user token & active session for userId: {}, session: {}", userId, tokenSessionId);
                        } else {
                            // User session was invalidated by another device login
                            log.warn("Session invalidated or logged in from another device for userId: {}, session: {}", userId, tokenSessionId);
                            isUser = "false";
                            newToken = jwtTokenProvider.generateGuestToken();
                            forcedLogout = true;
                        }
                    } else {
                        // User not found in database
                        log.warn("User ID {} from token not found in database", userId);
                        isUser = "false";
                        newToken = jwtTokenProvider.generateGuestToken();
                    }
                } catch (JwtException | NumberFormatException e) {
                    // guest token or malformed subject -> isUser stays "false"
                    log.info("Token is a guest token or has non-user subject: {}", e.getMessage());
                    isUser = "false";
                }
            } else {
                // Token is invalid/missing -> generate a new guest token to be returned in headers
                newToken = jwtTokenProvider.generateGuestToken();
                log.info("Token is invalid/missing, generated new guest token");
            }

            HttpHeaders headers = new HttpHeaders();
            if (newToken != null) {
                headers.set("token", newToken);
                headers.set("access_token", newToken);
            }

            // Identical to VerifyEmailLoginOtpController format (JSON string or "false")
            headers.set("isUser", isUser);
            headers.set("isuser", isUser);
            headers.set(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "token, access_token, isUser, isuser");

            Map<String, String> responseBody = new LinkedHashMap<>();

            if (forcedLogout) {
                responseBody.put("status", "error");
                responseBody.put("message", "Your account is logged on another device");
                responseBody.put("action", "forced_logout_device_limit_reached");
                responseBody.put("enum", "FORCED_LOGOUT_DEVICE_LIMIT_REACHED");
            } else {
                responseBody.put("status", "success");
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(responseBody);

        } catch (Exception e) {
            log.error("Unexpected error in /home endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "Failure"));
        }
    }

    private String buildIsUserHeader(UserDetailsDto user) throws Exception {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("user_id", user.userId());
        details.put("first_name", user.firstName());
        details.put("last_name", user.lastName());
        details.put("email", user.email());
        details.put("phone", user.phone());
        return objectMapper.writeValueAsString(details);
    }
}
