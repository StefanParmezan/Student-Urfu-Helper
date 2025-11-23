package urfu.student.helper.db.course.dto;

public record CourseAiDTO (
        String name,
        String category,
        String url
) implements Comparable<CourseAiDTO> {
    @Override
    public String toString() {
        return "Курс " + name + " из категории " + category + " имеет ссылку " + url;
    }

    @Override
    public int compareTo(CourseAiDTO o) {
        return name.compareTo(o.name());
    }
}
