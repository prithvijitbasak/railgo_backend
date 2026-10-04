package com.railgo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GuestTokenFilter extends OncePerRequestFilter {

    // Injects your existing JWT provider
    private final JwtTokenProvider jwtTokenProvider;

    public GuestTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        // If no token exists, issue a guest token
        if (token == null || token.isEmpty()) {
            // You will need to add a generateGuestToken() method to your JwtTokenProvider
            String newGuestToken = jwtTokenProvider.generateGuestToken();

            // Send the token back in a custom header
            response.setHeader("X-Guest-Token", newGuestToken);

            // CRITICAL: Explicitly expose this header so the frontend CORS allows reading it
            response.setHeader("Access-Control-Expose-Headers", "X-Guest-Token");
        }

        // Continue processing the request
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip this filter for all public API routes
        return path.startsWith("/api/public/");
    }
}