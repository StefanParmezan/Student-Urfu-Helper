package urfu.student.helper.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        logger.debug("Creating signing key from secret");
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email) {
        logger.info("Generating JWT token for email: {}", email);

        try {
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();

            logger.debug("JWT token generated successfully for email: {}", email);
            return token;

        } catch (Exception e) {
            logger.error("Error generating JWT token for email: {}", email, e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    public String extractEmail(String token) {
        logger.debug("Extracting email from JWT token");

        try {
            String email = extractAllClaims(token).getSubject();
            logger.debug("Successfully extracted email: {} from token", email);
            return email;

        } catch (Exception e) {
            logger.error("Error extracting email from JWT token", e);
            throw new RuntimeException("Failed to extract email from token", e);
        }
    }

    public boolean validateToken(String token) {
        logger.debug("Validating JWT token");

        if (token == null || token.trim().isEmpty()) {
            logger.warn("Token is null or empty");
            return false;
        }

        try {
            extractAllClaims(token);
            logger.debug("JWT token is valid");
            return true;

        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation", e);
            return false;
        }
    }

    public Date extractExpiration(String token) {
        logger.debug("Extracting expiration date from JWT token");

        try {
            Date expiration = extractAllClaims(token).getExpiration();
            logger.debug("Token expiration date: {}", expiration);
            return expiration;

        } catch (Exception e) {
            logger.error("Error extracting expiration date from JWT token", e);
            throw new RuntimeException("Failed to extract expiration date", e);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            boolean expired = expiration.before(new Date());
            if (expired) {
                logger.warn("JWT token is expired");
            }
            return expired;
        } catch (Exception e) {
            logger.error("Error checking token expiration", e);
            return true;
        }
    }

    private Claims extractAllClaims(String token) {
        logger.trace("Extracting all claims from JWT token");

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (JwtException e) {
            logger.error("JWT parsing error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during JWT parsing", e);
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }
}