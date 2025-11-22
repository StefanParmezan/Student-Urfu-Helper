package urfu.student.helper.security.jwt;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.student.StudentRepository;
import urfu.student.helper.security.dto.StudentAuthentificationPrincipal;

@Service
@AllArgsConstructor
public class StudentReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return Mono.fromCallable(() ->
                        studentRepository.findByEmail(email).orElse(null)
                )
                .map(StudentAuthentificationPrincipal::new)
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("Student not found by email: " + email)));
    }
}