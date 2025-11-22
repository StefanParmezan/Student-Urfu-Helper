package urfu.student.helper.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import urfu.student.helper.models.student.StudentEntity;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> getStudentById(Long id);
    Optional<StudentEntity> getStudentByEmail(String studentEmail);
    boolean existsByEmail(String studentEmail);
}