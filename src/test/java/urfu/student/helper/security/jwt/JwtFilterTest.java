package urfu.student.helper.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    private final Logger logger = LoggerFactory.getLogger(JwtFilterTest.class);

    @BeforeEach
    void setUp() {
        logger.info("Setting up JwtFilter test");
        jwtFilter = new JwtFilter();

        // Устанавливаем jwtService через reflection
        org.springframework.test.util.ReflectionTestUtils.setField(jwtFilter, "jwtService", jwtService);

        // Очищаем SecurityContext перед каждым тестом
        SecurityContextHolder.clearContext();
    }

    @Test
    void testValidTokenInHeader() throws ServletException, IOException {
        logger.info("=== Testing valid token in Authorization header ===");

        // Given
        String token = "valid.jwt.token";
        String email = "student@urfu.ru";

        when(request.getRequestURI()).thenReturn("/api/secure-endpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.isTokenExpired(token)).thenReturn(false);
        when(jwtService.extractEmail(token)).thenReturn(email);

        logger.info("Configured mock for valid token in header");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());

        logger.info("=== Valid token in header test completed successfully ===");
    }

    @Test
    void testInvalidToken() throws ServletException, IOException {
        logger.info("=== Testing invalid token ===");

        // Given
        String token = "invalid.jwt.token";

        when(request.getRequestURI()).thenReturn("/api/secure-endpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.validateToken(token)).thenReturn(false);

        logger.info("Configured mock for invalid token");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        logger.info("=== Invalid token test completed successfully ===");
    }

    @Test
    void testPublicEndpointWithoutToken() throws ServletException, IOException {
        logger.info("=== Testing public endpoint without token ===");

        // Given
        when(request.getRequestURI()).thenReturn("/student/login");

        logger.info("Configured mock for public endpoint");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        // SecurityContext должен остаться пустым, но без ошибок

        logger.info("=== Public endpoint test completed successfully ===");
    }

    @Test
    void testNoTokenForProtectedEndpoint() throws ServletException, IOException {
        logger.info("=== Testing protected endpoint without token ===");

        // Given
        when(request.getRequestURI()).thenReturn("/api/protected-endpoint");
        when(request.getHeader("Authorization")).thenReturn(null);

        logger.info("Configured mock for protected endpoint without token");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        logger.info("=== Protected endpoint without token test completed successfully ===");
    }

    @Test
    void testExpiredToken() throws ServletException, IOException {
        logger.info("=== Testing expired token ===");

        // Given
        String token = "expired.jwt.token";

        when(request.getRequestURI()).thenReturn("/api/secure-endpoint");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.isTokenExpired(token)).thenReturn(true);

        logger.info("Configured mock for expired token");

        // When
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        logger.info("=== Expired token test completed successfully ===");
    }
}