package urfu.student.helper.db.course;

import reactor.core.publisher.Flux;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

public interface CourseService {
    Flux<CourseEntity> getAllCourses();
    Flux<CourseEntity> getStudentCourses(StudentEntity student);
}
