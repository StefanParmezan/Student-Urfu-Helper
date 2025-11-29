package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.course.dto.CourseDTO;

import java.time.ZoneId;
import java.util.List;

public record StudentRegistryDTO(
        String studentFio,
        ZoneId timeZone,
        String educationStatus,
        String academicGroup,
        String studentNumber,
        String email,
        List<CourseDTO> courses
        ) {
}
