package urfu.student.helper.db.course;

import urfu.student.helper.db.parents.AiDto;

public record CourseAiDTO (
        Long id,
        String name
) implements AiDto {
    @Override
    public String toString() {
        return name;
    }
}
