package urfu.student.helper.db.student;

import org.mapstruct.Mapper;
import urfu.student.helper.db.student.dto.StudentAiDTO;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentAiDTO toAiDTO(StudentEntity entity);
}
