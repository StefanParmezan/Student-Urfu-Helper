package urfu.student.helper.db.student;

import org.mapstruct.Mapper;
import urfu.student.helper.db.student.dto.StudentAiDTO;
import urfu.student.helper.db.student.dto.StudentDTO;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentAiDTO toAiDTO(StudentEntity entity);
    StudentDTO toDto(StudentEntity entity);
    StudentEntity toEntity(StudentAiDTO aiDTO);
    StudentEntity toEntity(StudentDTO dto);
}
