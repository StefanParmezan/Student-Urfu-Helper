package urfu.student.helper.security.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.db.student.StudentMapper;
import urfu.student.helper.db.student.StudentRepository;
import urfu.student.helper.parser.ProfileParser;
import urfu.student.helper.security.dto.AuthRequest;
import urfu.student.helper.security.dto.AuthResponse;
import urfu.student.helper.security.jwt.JwtService;

@Transactional
@Service
@AllArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;

    private final StudentMapper mapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService service;


    public Mono<AuthResponse> save(AuthRequest authRequest){
        try (ProfileParser parser = new ProfileParser()) {
            return parser.parseStudentProfile(authRequest)
                    .doOnNext(dto -> {
                        StudentEntity student = mapper.toEntity(dto);
                        student.setPassword(passwordEncoder.encode(authRequest.password()));
                        studentRepository.save(student);
                    })
                    .map(student -> new AuthResponse(service.generateToken(student.email()), student));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
