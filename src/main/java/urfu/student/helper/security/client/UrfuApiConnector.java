package urfu.student.helper.security.client;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import urfu.student.helper.security.config.UrfuApiConfig;

@Component
@AllArgsConstructor
public class UrfuApiConnector {
    private static final Logger logger = LoggerFactory.getLogger(UrfuApiConnector.class);
    private final UrfuApiConfig urfuApiConfig;
    private final RestTemplate restTemplate;

    public String authenticateAndGetProfile(String email, String password) {
        logger.info("Attempting URFU authentication for email: {}", email);

        try {
            // 1. Аутентификация и получение сессионных cookies
            String sessionCookie = performAuthentication(email, password);

            // 2. Получение страницы профиля с использованием сессионных cookies
            String profileHtml = fetchProfilePage(sessionCookie);

            logger.info("Successfully retrieved URFU profile for email: {}", email);
            return profileHtml;

        } catch (RestClientException e) {
            logger.error("URFU authentication failed for email: {}", email, e);
            throw new RuntimeException("URFU authentication failed: " + e.getMessage(), e);
        }
    }

    private String performAuthentication(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", email);
        body.add("password", password);
        // Может потребоваться добавить другие параметры формы
        body.add("anchor", "");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(urfuApiConfig.getAuthUrl(), request, String.class);

        // Извлечение сессионных cookies из ответа
        return extractSessionCookies(response.getHeaders());
    }

    private String fetchProfilePage(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                urfuApiConfig.getProfileUrl(), HttpMethod.GET, request, String.class);

        return response.getBody();
    }

    private String extractSessionCookies(HttpHeaders headers) {
        // Извлекаем все cookies из заголовков ответа
        // В реальной реализации нужно будет анализировать конкретные cookies УрФУ
        String cookies = headers.getFirst(HttpHeaders.SET_COOKIE);
        logger.debug("Received cookies: {}", cookies);

        if (cookies == null || cookies.isEmpty()) {
            throw new RuntimeException("No session cookies received - authentication failed");
        }

        return cookies;
    }
}