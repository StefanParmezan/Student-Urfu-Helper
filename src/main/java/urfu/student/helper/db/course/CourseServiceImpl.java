package urfu.student.helper.db.course;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {
    @Override
    public List<CourseEntity> getAllCourses() {
        return List.of();
    }

    @Override
    public List<CourseEntity> getStudentCourses(StudentEntity student) {
        return List.of();
    }

    public Object getMarks(CourseEntity course) {
        return null;
    }
}
