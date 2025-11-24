package urfu.student.helper.parser;

import reactor.core.publisher.Flux;
import urfu.student.helper.db.course.dto.CourseDTO;

public interface CourseParser {
    Flux<CourseDTO> parse();
}
