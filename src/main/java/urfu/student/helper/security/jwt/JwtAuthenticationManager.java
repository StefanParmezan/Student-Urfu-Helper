package urfu.student.helper.security.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .cast(UsernamePasswordAuthenticationToken.class)
                .filter(auth -> auth.getCredentials() != null)
                .map(auth -> (String) auth.getCredentials())
                .filter(jwtProvider::validateToken)
                .map(token -> {
                    authentication.setAuthenticated(true);
                    return authentication;
                })
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid JWT token")));
    }
}