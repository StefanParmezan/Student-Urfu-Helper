package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.course.CourseAiDTO;
import urfu.student.helper.db.parents.AiDto;

import java.util.List;


public record StudentAiDTO  (
        Long id,
        String name,
        String educationStatus,
        List<CourseAiDTO> courses
) implements AiDto {
    @Override
    public String toString() {
        return """
                Студент %s обучается на %s на курсах %s
                """
                .formatted(name, educationStatus, collectionToString(courses));
    }
}
