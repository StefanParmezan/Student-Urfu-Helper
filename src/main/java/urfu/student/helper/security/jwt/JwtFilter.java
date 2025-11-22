package urfu.student.helper.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        logger.debug("JWT Filter processing {} request to: {}", method, requestPath);

        // Пропускаем публичные endpoints
        if (isPublicEndpoint(requestPath)) {
            logger.debug("Skipping JWT filter for public endpoint: {}", requestPath);
            chain.doFilter(request, response);
            return;
        }

        String token = getTokenFromRequest(request);

        if (token != null) {
            logger.debug("JWT token found in request");

            if (jwtService.validateToken(token) && !jwtService.isTokenExpired(token)) {
                String email = jwtService.extractEmail(token);
                logger.debug("Valid JWT token for user: {}", email);

                try {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    null,
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"))
                            );

                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("Authentication set in SecurityContext for user: {}", email);

                } catch (Exception e) {
                    logger.error("Error setting authentication for user: {}", email, e);
                    SecurityContextHolder.clearContext();
                }
            } else {
                logger.warn("Invalid or expired JWT token");
                SecurityContextHolder.clearContext();
            }
        } else {
            logger.debug("No JWT token found in request");

            // Для защищенных endpoints без токена - очищаем контекст
            if (!isPublicEndpoint(requestPath)) {
                logger.warn("Access attempt to protected endpoint without JWT token: {}", requestPath);
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        // Приоритет: Header > Cookie
        String headerToken = getTokenFromHeader(request);
        if (headerToken != null) {
            return headerToken;
        }

        return getTokenFromCookie(request);
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            logger.trace("Found JWT token in Authorization header");
            return token;
        }
        return null;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    logger.trace("Found JWT token in cookies");
                    return token;
                }
            }
        }
        return null;
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/student/login") ||
                path.startsWith("/api/public/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/error") ||
                path.equals("/favicon.ico");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldNotFilter = isPublicEndpoint(path);

        if (shouldNotFilter) {
            logger.trace("Skipping JWT filter for path: {}", path);
        }

        return shouldNotFilter;
    }
}