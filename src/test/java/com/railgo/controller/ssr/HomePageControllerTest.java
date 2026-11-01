package com.railgo.controller.ssr;

import com.railgo.config.JwtTokenProvider;
import com.railgo.entity.UserEntity;
import com.railgo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomePageControllerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserService userService;

    private HomePageController homePageController;

    @BeforeEach
    void setUp() {
        homePageController = new HomePageController(jwtTokenProvider, userService);
    }

    @Test
    void testHome_WhenActiveUserSessionMatchesDb() {
        String token = "valid-token";
        Long userId = 101L;
        String sessionId = "session-device-1";

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("1234567890");
        user.setCurrentSessionId(sessionId);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserId(token)).thenReturn(userId);
        when(jwtTokenProvider.getSessionId(token)).thenReturn(sessionId);
        when(userService.findById(userId)).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> response = homePageController.home(token, null, null);

        assertEquals(200, response.getStatusCode().value());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("success", body.get("status"));
        assertNull(body.get("action"));
        assertNull(body.get("enum"));
        assertNull(body.get("message"));

        assertTrue(response.getHeaders().getFirst("isUser").contains("user@example.com"));
    }

    @Test
    void testHome_WhenLoggedInOnAnotherDevice_ShouldReturnForcedLogoutDetails() {
        String token = "device1-token";
        Long userId = 101L;
        String device1SessionId = "session-device-1";
        String device2SessionId = "session-device-2"; // Updated in DB by device 2

        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setEmail("user@example.com");
        user.setCurrentSessionId(device2SessionId);

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUserId(token)).thenReturn(userId);
        when(jwtTokenProvider.getSessionId(token)).thenReturn(device1SessionId);
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateGuestToken()).thenReturn("guest-token-xyz");

        ResponseEntity<Map<String, String>> response = homePageController.home(token, null, null);

        assertEquals(200, response.getStatusCode().value());
        Map<String, String> body = response.getBody();
        assertNotNull(body);

        assertEquals("error", body.get("status"));
        assertEquals("forced_logout_device_limit_reached", body.get("action"));
        assertEquals("FORCED_LOGOUT_DEVICE_LIMIT_REACHED", body.get("enum"));
        assertNull(body.get("enumValue"));
        assertEquals("Your account is logged on another device", body.get("message"));

        assertEquals("false", response.getHeaders().getFirst("isUser"));
        assertEquals("false", response.getHeaders().getFirst("isuser"));
        assertEquals("guest-token-xyz", response.getHeaders().getFirst("token"));
        assertEquals("guest-token-xyz", response.getHeaders().getFirst("access_token"));
    }

    @Test
    void testHome_WhenTokenMissingOrGuest() {
        when(jwtTokenProvider.generateGuestToken()).thenReturn("new-guest-token");

        ResponseEntity<Map<String, String>> response = homePageController.home(null, null, null);

        assertEquals(200, response.getStatusCode().value());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("success", body.get("status"));
        assertNull(body.get("action"));
        assertNull(body.get("enum"));
        assertNull(body.get("message"));

        assertEquals("false", response.getHeaders().getFirst("isUser"));
        assertEquals("new-guest-token", response.getHeaders().getFirst("token"));
    }
}
