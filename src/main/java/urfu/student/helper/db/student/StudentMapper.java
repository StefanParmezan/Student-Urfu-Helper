package urfu.student.helper.db.student;

import org.mapstruct.Mapper;
import urfu.student.helper.db.student.dto.StudentAiDTO;
import urfu.student.helper.db.student.dto.StudentDTO;
import urfu.student.helper.db.student.dto.StudentRegistryDTO;

import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentAiDTO toAiDTO(StudentEntity entity);
    StudentDTO toDto(StudentEntity entity);
    StudentEntity toEntity(StudentAiDTO aiDTO);
    StudentEntity toEntity(StudentDTO dto);
    StudentEntity toEntity(StudentRegistryDTO dto);
    

    default String map(ZoneId zoneId) {
        return zoneId != null ? zoneId.getId() : null;
    }

    default ZoneId map(String zoneId) {
        return zoneId != null ? ZoneId.of(zoneId) : null;
    }
}