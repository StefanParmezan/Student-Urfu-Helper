package urfu.student.helper.db.course;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import urfu.student.helper.db.course.dto.CourseAiDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {
    CourseAiDTO toAiDto(CourseEntity entity);
}
