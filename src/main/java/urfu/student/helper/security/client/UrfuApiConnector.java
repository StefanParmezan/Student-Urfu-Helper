package urfu.student.helper.security.client;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import urfu.student.helper.security.config.UrfuApiConfig;
import urfu.student.helper.security.dto.AuthRequest;

import java.util.function.Function;

@Log4j
@Component
@AllArgsConstructor
public class UrfuApiConnector {
    private final UrfuApiConfig config;
    private final WebClient client = WebClient.builder()
            .baseUrl(config.getBaseUrl())
            .build();

    public Mono<String> authenticate(AuthRequest credentials) {
        return auth(credentials).flatMap(this::getProfile);
    }

    private Mono<String> auth(AuthRequest credentials) {
        return client.post()
                .uri(config.getAuthEndpoint())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(credentials)
                .exchangeToMono(clientResponse -> Mono.just(clientResponse.headers()))
                .map(headers -> headers.asHttpHeaders().getFirst(HttpHeaders.SET_COOKIE));
    }

    private Mono<String> getProfile(String cookies) {
        return client.post()
                .uri(config.getProfileEndpoint())
                .header(HttpHeaders.COOKIE, cookies)
                .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class));
    }
}