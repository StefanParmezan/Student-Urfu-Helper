package urfu.student.helper.db.student.dto;

public record StudentRegistryDTO(
        String studentFio,
        String timeZone,
        String educationStatus,
        String academicGroup,
        String studentNumber,
        String studentEmail
        ) {
        @Override
        public String toString() {
                return "StudentRegistryDTO{" +
                        "studentFio='" + studentFio + '\'' +
                        ", timeZone='" + timeZone + '\'' +
                        ", educationStatus='" + educationStatus + '\'' +
                        ", academicGroup='" + academicGroup + '\'' +
                        ", studentNumber='" + studentNumber + '\'' +
                        ", studentEmail='" + studentEmail + '\'' +
                        '}';
        }
}
