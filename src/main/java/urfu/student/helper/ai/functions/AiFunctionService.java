package urfu.student.helper.ai.functions;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.course.CourseMapper;
import urfu.student.helper.db.course.CourseService;
import urfu.student.helper.db.course.dto.CourseAiDTO;
import urfu.student.helper.db.parents.CollectionToAiStringAdapter;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class AiFunctionService {
    private final CourseService courseService;
    private final CourseMapper mapper;

    public Mono<String> getAllCourses() {
        return courseService.getAllCourses()
                .map(mapper::toAiDto)
                .sort()
                .collectList()
                .map(this::mapToString);
    }

    public Mono<String> getStudentCourses(StudentEntity student) {
        return courseService.getStudentCourses(student)
                .map(mapper::toAiDto)
                .sort()
                .collectList()
                .map(this::mapToString);
    }

    public String mapToString(List<CourseAiDTO> list) {
        if (list.isEmpty()) {
            return "По такому имени не найдено не одного курса. Попробуй запросить еще раз с конкретным именем";
        }

        if (list.size() == 1) {
            return list.stream().findAny().get().toString();
        }

        return "Возможно вы имели в виду: " + new CollectionToAiStringAdapter(list);
    }
}
