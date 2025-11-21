package urfu.student.helper.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import urfu.student.helper.models.student.Student;
import urfu.student.helper.models.student.dto.StudentRegistryDTO;
import urfu.student.helper.repositories.StudentRepository;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder){
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Student getStudentById(Long id){
        return studentRepository.getStudentById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student with id `%s` not found".formatted(id)));
    }

    public Student save(StudentRegistryDTO studentRegistryDTO){
        Student student = new Student(studentRegistryDTO.studentName(), studentRegistryDTO.studentSurName(), passwordEncoder.encode(studentRegistryDTO.password()), studentRegistryDTO.phoneNumber());
        return studentRepository.save(student);
    }
}
