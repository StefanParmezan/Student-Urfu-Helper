package urfu.student.helper.security.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UrfuApiConnectorTest {

    @Mock
    private RestTemplate restTemplate;

    private UrfuApiConnector urfuApiConnector;

    private final String TEST_EMAIL = "beerklaro@bk.ru";
    private final String TEST_PASSWORD = "B0rzhchling5vaa789";
    private final String AUTH_URL = "https://elearn.urfu.ru/login/index.php";
    private final String PROFILE_URL = "https://elearn.urfu.ru/user/profile.php";
    private final String SESSION_COOKIE = "MoodleSession=abc123; path=/";
    private final String PROFILE_HTML = "<html>User Profile Page</html>";

    @BeforeEach
    void setUp() {
        log.info("Setting up test for UrfuApiConnector");
        urfuApiConnector = new UrfuApiConnector(restTemplate);

        // Set URLs through reflection since @Value won't work in tests
        setField(urfuApiConnector, "authUrl", AUTH_URL);
        setField(urfuApiConnector, "profileUrl", PROFILE_URL);
    }

    private void setField(UrfuApiConnector connector, String fieldName, String value) {
        try {
            var field = UrfuApiConnector.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(connector, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }


    @Test
    @DisplayName("Ошибка аутентификации - должен выбросить исключение")
    void authenticateAndGetProfile_AuthFailure() {
        log.info("Starting test: Ошибка аутентификации");

        when(restTemplate.postForEntity(eq(AUTH_URL), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RestClientException("Connection failed"));

        // Execute & Verify
        log.warn("Expecting RuntimeException due to authentication failure");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> urfuApiConnector.authenticateAndGetProfile(TEST_EMAIL, TEST_PASSWORD));

        assertTrue(exception.getMessage().contains("URFU authentication failed"));

        // Исправлено: используем конкретные матчеры вместо any()
        verify(restTemplate, never()).exchange(
                eq(PROFILE_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class));

        log.info("Test completed - исключение выброшено корректно");
    }

    @Test
    @DisplayName("Ошибка при получении профиля после успешной аутентификации")
    void authenticateAndGetProfile_ProfileFetchFailure() {
        log.info("Starting test: Ошибка при получении профиля после успешной аутентификации");

        // Mock successful authentication
        ResponseEntity<String> authResponse = mockAuthResponse();
        log.debug("Mocked successful authentication response");

        when(restTemplate.postForEntity(eq(AUTH_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(authResponse);
        when(restTemplate.exchange(
                eq(PROFILE_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenThrow(new RestClientException("Profile unavailable"));

        // Execute & Verify
        log.warn("Expecting RuntimeException due to profile fetch failure");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> urfuApiConnector.authenticateAndGetProfile(TEST_EMAIL, TEST_PASSWORD));

        assertTrue(exception.getMessage().contains("URFU authentication failed"));
        verify(restTemplate, times(1)).postForEntity(eq(AUTH_URL), any(HttpEntity.class), eq(String.class));
        verify(restTemplate, times(1)).exchange(
                eq(PROFILE_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class));

        log.info("Test completed - исключение при получении профиля обработано корректно");
    }

    @Test
    @DisplayName("Аутентификация возвращает пустые cookies")
    void performAuthentication_EmptyCookies() {
        log.info("Starting test: Аутентификация возвращает пустые cookies");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.SET_COOKIE, ""); // Пустые cookies

        ResponseEntity<String> authResponse = new ResponseEntity<>("Auth failed", headers, HttpStatus.OK);

        when(restTemplate.postForEntity(eq(AUTH_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(authResponse);

        // Execute & Verify
        log.warn("Expecting RuntimeException due to empty cookies");
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> urfuApiConnector.authenticateAndGetProfile(TEST_EMAIL, TEST_PASSWORD));

        assertTrue(exception.getMessage().contains("No session cookies received"));

        // Исправлено: используем конкретные матчеры
        verify(restTemplate, never()).exchange(
                eq(PROFILE_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class));

        log.info("Test completed - исключение при пустых cookies обработано корректно");
    }

    @Test
    @DisplayName("Проверка корректности параметров аутентификации")
    void performAuthentication_ValidParameters() {
        log.info("Starting test: Проверка корректности параметров аутентификации");

        // Mock response
        ResponseEntity<String> authResponse = mockAuthResponse();
        when(restTemplate.postForEntity(eq(AUTH_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(authResponse);

        // Execute
        urfuApiConnector.authenticateAndGetProfile(TEST_EMAIL, TEST_PASSWORD);

        // Capture request parameters
        ArgumentCaptor<HttpEntity<MultiValueMap<String, String>>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<MultiValueMap<String, String>>>) (Class<?>) HttpEntity.class);

        verify(restTemplate).postForEntity(eq(AUTH_URL), requestCaptor.capture(), eq(String.class));

        HttpEntity<MultiValueMap<String, String>> capturedRequest = requestCaptor.getValue();

        // Verify request composition
        assertNotNull(capturedRequest, "Запрос не должен быть null");

        MultiValueMap<String, String> body = capturedRequest.getBody();
        assertNotNull(body, "Тело запроса не должно быть null");
        assertEquals(TEST_EMAIL, body.getFirst("username"), "Email должен совпадать");
        assertEquals(TEST_PASSWORD, body.getFirst("password"), "Пароль должен совпадать");
        assertEquals("", body.getFirst("anchor"), "Anchor должен быть пустым");

        HttpHeaders headers = capturedRequest.getHeaders();
        assertEquals(MediaType.APPLICATION_FORM_URLENCODED, headers.getContentType(),
                "Content-Type должен быть form-urlencoded");

        log.info("Test completed - параметры аутентификации корректны");
    }

    @Test
    @DisplayName("Проверка заголовков при запросе профиля")
    void fetchProfilePage_ValidHeaders() {
        log.info("Starting test: Проверка заголовков при запросе профиля");

        // Mock responses
        ResponseEntity<String> authResponse = mockAuthResponse();
        ResponseEntity<String> profileResponse = mockProfileResponse();

        when(restTemplate.postForEntity(eq(AUTH_URL), any(HttpEntity.class), eq(String.class)))
                .thenReturn(authResponse);
        when(restTemplate.exchange(
                eq(PROFILE_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(profileResponse);

        // Execute
        urfuApiConnector.authenticateAndGetProfile(TEST_EMAIL, TEST_PASSWORD);

        // Capture profile request
        ArgumentCaptor<HttpEntity<String>> requestCaptor =
                ArgumentCaptor.forClass((Class<HttpEntity<String>>) (Class<?>) HttpEntity.class);

        verify(restTemplate).exchange(
                eq(PROFILE_URL),
                eq(HttpMethod.GET),
                requestCaptor.capture(),
                eq(String.class));

        HttpEntity<String> capturedRequest = requestCaptor.getValue();

        // Verify headers
        assertNotNull(capturedRequest, "Запрос профиля не должен быть null");

        HttpHeaders headers = capturedRequest.getHeaders();
        assertTrue(headers.containsKey("Cookie"), "Должен содержать Cookie заголовок");
        assertTrue(headers.containsKey("User-Agent"), "Должен содержать User-Agent заголовок");
        assertEquals(SESSION_COOKIE, headers.getFirst("Cookie"), "Cookie должен совпадать");

        log.info("Test completed - заголовки запроса профиля корректны");
    }

    private ResponseEntity<String> mockAuthResponse() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.SET_COOKIE, SESSION_COOKIE);
        return new ResponseEntity<>("Authentication successful", headers, HttpStatus.OK);
    }

    private ResponseEntity<String> mockProfileResponse() {
        return new ResponseEntity<>(PROFILE_HTML, HttpStatus.OK);
    }
}