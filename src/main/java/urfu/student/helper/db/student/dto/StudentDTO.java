package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.db.course.dto.CourseDTO;

import java.time.ZoneId;
import java.util.List;

public record StudentDTO(
        String fio,
        String email,
        String timeZone,
        StudentEntity.EducationStatus educationStatus,
        String academicGroup) {
}
