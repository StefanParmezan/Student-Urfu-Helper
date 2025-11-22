package urfu.student.helper.db.course;

import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

public interface CourseService {
    List<CourseEntity> getAllCourses();
    List<CourseEntity> getStudentCourses(StudentEntity student);
}
