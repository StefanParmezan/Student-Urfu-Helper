package urfu.student.helper.db.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> getStudentById(Long id);
    Optional<StudentEntity> getStudentByEmail(String studentEmail);
    boolean existsByEmail(String studentEmail);
    StudentEntity save(Mono<StudentEntity> studentEntityMono);
    Optional<StudentEntity> findByEmail(String email);
}