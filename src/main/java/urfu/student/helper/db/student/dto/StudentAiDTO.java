package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.course.dto.CourseAiDTO;
import urfu.student.helper.db.parents.CollectionToAiStringAdapter;

import java.util.List;


public record StudentAiDTO  (
        Long id,
        String name,
        String educationStatus,
        List<CourseAiDTO> courses
) {
    @Override
    public String toString() {
        return """
                Студент %s обучается на %s на курсах %s
                """
                .formatted(name, educationStatus, new CollectionToAiStringAdapter(courses));
    }
}
