package urfu.student.helper.db.course;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import urfu.student.helper.db.course.dto.CourseAiDTO;
import urfu.student.helper.db.course.dto.CourseDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {
    CourseAiDTO toAiDto(CourseEntity entity);
    CourseDTO toDto(CourseEntity entity);
    CourseEntity toEntity(CourseAiDTO aiDTO);
    CourseEntity toEntity(CourseDTO dto);
}
