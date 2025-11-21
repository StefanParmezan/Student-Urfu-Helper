package urfu.student.helper.security.dto;

import java.util.List;

public record StudentProfileDto(
        String fullName,
        String email,
        String timeZone,
        String educationStatus,
        String academicGroup,
        String studentNumber,
        List<CourseDto> courses
) {}
