package urfu.student.helper.db.course.dto;

public record CourseAiDTO (
        Long id,
        String name
) implements Comparable<CourseAiDTO> {
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(CourseAiDTO o) {
        return name.compareTo(o.name());
    }
}
