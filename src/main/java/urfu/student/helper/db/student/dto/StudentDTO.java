package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.student.StudentEntity;

public record StudentDTO(
        String fio,
        String email,
        String timeZone,
        StudentEntity.EducationStatus educationStatus,
        String academicGroup) {
}
