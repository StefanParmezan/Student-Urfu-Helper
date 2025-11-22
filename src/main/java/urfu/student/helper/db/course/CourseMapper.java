package urfu.student.helper.db.course;

import org.mapstruct.Mapper;
import urfu.student.helper.db.course.dto.CourseAiDTO;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseAiDTO toAiDto(CourseEntity entity);
}
