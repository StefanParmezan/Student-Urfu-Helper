package urfu.student.helper.ai;

import reactor.core.publisher.Flux;
import urfu.student.helper.models.student.Student;

public interface AiService {
    Flux<String> call(String userRequest, Student user);
}
