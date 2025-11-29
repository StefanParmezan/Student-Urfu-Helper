package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.course.dto.CourseDTO;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

public record StudentRegistryDTO(
        String studentFio,
        String timeZone,
        StudentEntity.EducationStatus educationStatus,
        String academicGroup,
        String studentNumber,
        String email,
        List<CourseDTO> courses
        ) {
}
