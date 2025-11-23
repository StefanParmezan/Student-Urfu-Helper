package urfu.student.helper.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.student.dto.StudentDTO;
import urfu.student.helper.security.dto.AuthRequest;
import urfu.student.helper.security.dto.AuthResponse;
import urfu.student.helper.security.service.AuthService;

@RestController("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping
    public Mono<StudentDTO> auth(@RequestBody AuthRequest data) {
        return service.save(data);
    }
}
