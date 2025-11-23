package urfu.student.helper.db.student.dto;

import urfu.student.helper.security.dto.CourseDto;

import java.util.List;

public record StudentRegistryDTO(
        String studentFio,
        String timeZone,
        String educationStatus,
        String academicGroup,
        String studentNumber,
        String studentEmail,
        List<CourseDto> courses
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
                        ", courses=" + courses +
                        '}';
        }
}
