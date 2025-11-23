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
import urfu.student.helper.security.dto.CourseDto;
import urfu.student.helper.security.parser.ProfileParser;

import java.util.List;

@Transactional
@Service
@AllArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;

    private final PasswordEncoder passwordEncoder;


    public StudentDTO save(AuthRequest authRequest){
        try (ProfileParser parser = new ProfileParser()) {
            Mono<StudentRegistryDTO> studentRegistryDTO = parser.parseStudentProfile(authRequest);
            String password = passwordEncoder.encode(authRequest.password());
            Mono<StudentEntity> student = studentRegistryDTO.map((studentRegistryDTO1) -> new StudentEntity(
                    studentRegistryDTO1.studentFio(),
                    password,
                    studentRegistryDTO1.timeZone(),
                    StudentEntity.EducationStatus.getByName(studentRegistryDTO1.educationStatus()),
                    studentRegistryDTO1.academicGroup(),
                    studentRegistryDTO1.studentNumber(),
                    studentRegistryDTO1.studentEmail(),
                    studentRegistryDTO1.courses().stream().map(s -> s.of(s.name(), s.courseCategory(), s.url())).toList()
            ));
            return StudentDTO.of(studentRepository.save(student));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
