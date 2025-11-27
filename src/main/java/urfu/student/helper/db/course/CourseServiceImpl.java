package urfu.student.helper.db.course;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;


//Степан 27.11.25 я тут переделал возвращаемый тип у методов и List.of чтоб аппка запустилась
//TODO: переписать возвращаемый тип методов
@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {
    @Override
    public Flux<CourseEntity> getAllCourses() {
        return (Flux<CourseEntity>) List.of();
    }

    @Override
    public Flux<CourseEntity> getStudentCourses(StudentEntity student) {
        return (Flux<CourseEntity>) List.of();
    }

    public Object getMarks(CourseEntity course) {
        return null;
    }
}
