package urfu.student.helper.ai.functions.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import urfu.student.helper.db.course.CourseEntity;
import urfu.student.helper.db.course.dto.CourseAiDTO;
import urfu.student.helper.db.course.CourseMapper;
import urfu.student.helper.db.course.CourseService;
import urfu.student.helper.db.parents.CollectionToAiStringAdapter;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class AiFunctionServiceImpl implements AiFunctionService {
    private final CourseService courseService;
    private final CourseMapper mapper;

    @Override
    public List<CourseAiDTO> getAllCourses() {
        return courseService.getAllCourses()
                .stream()
                .map(mapper::toAiDto)
                .sorted()
                .toList();
    }

    @Override
    public List<CourseAiDTO> getStudentCourses(StudentEntity student) {
        return courseService.getStudentCourses(student)
                .stream()
                .map(mapper::toAiDto)
                .sorted()
                .toList();
    }

    public <T> String getSomeByCourse(String aiReturnedCourseName, Function<CourseEntity, T> mapperFunction) {
        var list = courseService.getAllCourses()
                .stream()
                .filter(course -> {
                    String realCourseName = course.getCourseName();
                    return realCourseName.contains(aiReturnedCourseName) ||
                            aiReturnedCourseName.contains(realCourseName);
                })
                .map(mapperFunction)
                .toList();

        if (list.isEmpty()) {
            return "По такому имени не найдено не одного курса. Попробуй запросить еще раз с конкретным именем";
        }

        if (list.size() == 1) {
            return list.stream().findAny().get().toString();
        }

        return "Возможно вы имели в виду: " + new CollectionToAiStringAdapter(list);
    }
}
