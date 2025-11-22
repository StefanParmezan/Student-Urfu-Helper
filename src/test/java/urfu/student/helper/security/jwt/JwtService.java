package urfu.student.helper.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    private final Logger logger = LoggerFactory.getLogger(JwtServiceTest.class);

    @BeforeEach
    void setUp() {
        logger.info("Setting up JwtService test");
        jwtService = new JwtService();

        // Устанавливаем секрет и expiration из твоих переменных
        ReflectionTestUtils.setField(jwtService, "secret",
                "superduperultramegasecretkeyforJWT228666777");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24 часа
    }

    @Test
    void testGenerateAndValidateToken() {
        logger.info("=== Testing token generation and validation ===");

        // Given
        String email = "test@urfu.ru";

        // When
        String token = jwtService.generateToken(email);
        logger.info("Generated token: {}", token);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtService.validateToken(token));
        assertEquals(email, jwtService.extractEmail(token));
        assertFalse(jwtService.isTokenExpired(token));

        logger.info("=== Token generation test completed successfully ===");
    }

    @Test
    void testInvalidToken() {
        logger.info("=== Testing invalid token validation ===");

        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertFalse(jwtService.validateToken(invalidToken));
        assertThrows(RuntimeException.class, () -> jwtService.extractEmail(invalidToken));

        logger.info("=== Invalid token test completed successfully ===");
    }

    @Test
    void testNullAndEmptyToken() {
        logger.info("=== Testing null and empty token validation ===");

        // When & Then
        assertFalse(jwtService.validateToken(null));
        assertFalse(jwtService.validateToken(""));
        assertFalse(jwtService.validateToken("   "));

        logger.info("=== Null/empty token test completed successfully ===");
    }

    @Test
    void testTokenWithDifferentEmail() {
        logger.info("=== Testing token with different emails ===");

        // Given
        String email1 = "student1@urfu.ru";
        String email2 = "student2@urfu.ru";

        // When
        String token1 = jwtService.generateToken(email1);
        String token2 = jwtService.generateToken(email2);

        // Then
        assertEquals(email1, jwtService.extractEmail(token1));
        assertEquals(email2, jwtService.extractEmail(token2));
        assertNotEquals(token1, token2);

        logger.info("=== Different emails test completed successfully ===");
    }

    @Test
    void testTokenExpiration() {
        logger.info("=== Testing token expiration ===");

        // Given
        String email = "test@urfu.ru";

        // Создаем JwtService с очень коротким expiration
        JwtService shortLivedJwtService = new JwtService();
        ReflectionTestUtils.setField(shortLivedJwtService, "secret",
                "superduperultramegasecretkeyforJWT228666777");
        ReflectionTestUtils.setField(shortLivedJwtService, "expiration", 1L); // 1 ms

        // When
        String token = shortLivedJwtService.generateToken(email);

        // Ждем немного, чтобы токен истек
        try {
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then
        assertTrue(shortLivedJwtService.isTokenExpired(token));
        assertFalse(shortLivedJwtService.validateToken(token));

        logger.info("=== Token expiration test completed successfully ===");
    }
}