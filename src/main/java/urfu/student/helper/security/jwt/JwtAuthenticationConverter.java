package urfu.student.helper.security.jwt;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    private final JwtService jwtProvider;
    private final StudentReactiveUserDetailsService userDetailsService;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest())
                .flatMap(request -> Mono.justOrEmpty(
                        request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)
                ))
                .filter(header -> header.startsWith(BEARER_PREFIX))
                .map(header -> header.substring(BEARER_PREFIX.length()))
                .flatMap(token -> {
                    String username = jwtProvider.getUsernameFromToken(token);
                    return userDetailsService.findByUsername(username)
                            .map(userDetails -> new JwtAuthToken(userDetails, token));
                });
    }
}