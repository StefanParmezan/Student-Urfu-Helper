package urfu.student.helper.db.student.dto;

import urfu.student.helper.db.student.StudentEntity;
import urfu.student.helper.security.dto.CourseDto;

import java.time.ZoneId;
import java.util.List;

public record StudentDTO(
        String studentFio,
        ZoneId timeZone,
        StudentEntity.EducationStatus educationStatus,
        String academicGroup,
        List<CourseDto> courseDtoList) {
    public static StudentDTO of(StudentEntity student){
        return new StudentDTO(student.getStudentFio(), student.getTimeZone(), student.getEducationStatus(), student.getAcademicGroup(), student.getCourseEntityList().stream().map(s -> new CourseDto(s.getCourseName(), s.getCourseCategory(), s.getCourseUrl())).toList());
    }
}
