package urfu.student.helper.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import urfu.student.helper.security.client.UrfuApiConnector;
import urfu.student.helper.security.dto.AuthRequest;
import urfu.student.helper.security.dto.AuthResponse;
import urfu.student.helper.security.service.AuthService;

@RestController("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/fuckingphp")
    public Exception fuckingPhp() {
        return new RuntimeException("THE_PHP_FUCKED_OUR_ASS");
    }

    @PostMapping
    public Mono<AuthResponse> auth(@RequestBody AuthRequest data) {
        return service.fuckingPhpStudentSaveAndAuth(data);
    }
}
