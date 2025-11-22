package urfu.student.helper.ai.functions;

import urfu.student.helper.db.course.CourseAiDTO;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

/**
 * Главный интерфейс и точка горячей замены реализаций
 */
public interface CourseService {
    List<CourseAiDTO> getGeneralCourses();
    List<CourseAiDTO> getStudentCourses(StudentEntity student);
    String getCourseDescription(String courseName);
}
