package urfu.student.helper.db.course;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseAiDTO toAiDto(CourseEntity entity);
}
