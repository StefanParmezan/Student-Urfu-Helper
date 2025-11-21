package urfu.student.helper.security.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class UrfuAuthClient {

    private static final Logger logger = LoggerFactory.getLogger(UrfuAuthClient.class);

    @Value("${urfu.auth.url:https://elearn.urfu.ru/login/index.php}")
    private String authUrl;

    @Value("${urfu.profile.url:https://elearn.urfu.ru/user/profile.php}")
    private String profileUrl;

    private final RestTemplate restTemplate;

    public UrfuAuthClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String authenticateAndGetProfile(String email, String password) {
        try {
            logger.info("Attempting authentication for email: {}", email);

            // 1. Аутентификация
            String sessionCookie = performAuthentication(email, password);

            // 2. Получение профиля с сессионной кукой
            return fetchProfilePage(sessionCookie);

        } catch (RestClientException e) {
            logger.error("Authentication failed for email: {}", email, e);
            throw new RuntimeException("URFU authentication failed", e);
        }
    }

    private String performAuthentication(String email, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", email);
        body.add("password", password);
        // Добавить другие необходимые параметры формы

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);

        // Извлечение сессионной куки из ответа
        return extractSessionCookie(response.getHeaders());
    }

    private String fetchProfilePage(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionCookie);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                profileUrl, HttpMethod.GET, request, String.class);

        return response.getBody();
    }

    private String extractSessionCookie(HttpHeaders headers) {
        // Логика извлечения сессионной куки из заголовков
        // Это нужно будет настроить на основе реального ответа УрФУ
        return headers.getFirst(HttpHeaders.SET_COOKIE);
    }
}