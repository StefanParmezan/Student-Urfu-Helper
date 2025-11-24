package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.course.dto.CourseDTO;

import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.util.List;

public record StudentRegistryDTO(
        String studentFio,
        ZoneId timeZone,
        String educationStatus,
        String academicGroup,
        String studentNumber,
        String studentEmail,
        List<CourseDTO> courses
        ) {
        @Override
        @NotNull
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
