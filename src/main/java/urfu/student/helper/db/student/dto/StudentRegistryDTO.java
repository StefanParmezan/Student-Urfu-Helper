package urfu.student.helper.db.student.dto;

public record StudentRegistryDTO(
        String studentName,
        String studentSurName,
        String patronymic,
        String timeZone,
        String educationStatus,
        String academicGroup,
        String studentNumber,
        String studentEmail
        ) {
}
