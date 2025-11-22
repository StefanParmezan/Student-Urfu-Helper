package urfu.student.helper.ai.functions.service;

import urfu.student.helper.db.course.dto.CourseAiDTO;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

/**
 * Главный интерфейс и точка горячей замены реализаций
 */
public interface AiFunctionService {
    List<CourseAiDTO> getAllCourses();
    List<CourseAiDTO> getStudentCourses(StudentEntity student);
}
