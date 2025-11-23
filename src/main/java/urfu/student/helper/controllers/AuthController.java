package urfu.student.helper.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.student.dto.StudentDTO;
import urfu.student.helper.security.dto.AuthRequest;
import urfu.student.helper.security.dto.AuthResponse;
import urfu.student.helper.security.service.AuthService;

import java.awt.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<AuthResponse> auth(@RequestBody AuthRequest data) {
        return service.save(data);
    }
}
