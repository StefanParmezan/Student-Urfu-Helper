package urfu.student.helper.security.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.course.CourseEntity;
import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.db.student.StudentRepository;
import urfu.student.helper.db.student.dto.StudentDTO;
import urfu.student.helper.db.student.dto.StudentRegistryDTO;
import urfu.student.helper.security.dto.AuthRequest;
import urfu.student.helper.security.dto.AuthResponse;
import urfu.student.helper.security.dto.CourseDto;
import urfu.student.helper.security.jwt.JwtService;
import urfu.student.helper.security.parser.ProfileParser;

import java.util.List;

@Transactional
@Service
@AllArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService service;


    public Mono<AuthResponse> save(AuthRequest authRequest){
        try (ProfileParser parser = new ProfileParser()) {
            Mono<StudentEntity> studentMono = parser.parseStudentProfile(authRequest);
            String password = passwordEncoder.encode(authRequest.password());
            return studentMono.doOnNext(student -> student.setPassword(passwordEncoder.encode(student.getPassword())))
                    .map(studentRepository::save)
                    .map(student -> new AuthResponse(service.generateToken(student.getEmail()), StudentDTO.of(student)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
