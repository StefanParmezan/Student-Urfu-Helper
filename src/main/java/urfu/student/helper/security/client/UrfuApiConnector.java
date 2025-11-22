package urfu.student.helper.security.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import urfu.student.helper.security.config.UrfuApiConfig;
import urfu.student.helper.security.dto.AuthRequest;

@Slf4j
@Component
public class UrfuApiConnector {
    private final UrfuApiConfig config;
    private final WebClient client;

    public UrfuApiConnector(UrfuApiConfig config) {
        this.config = config;
        this.client = WebClient.builder()
                .baseUrl(config.getBaseUrl())
                .build();
    }

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