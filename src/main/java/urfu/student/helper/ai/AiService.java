package urfu.student.helper.ai;

import reactor.core.publisher.Flux;
import urfu.student.helper.models.student.Student;

public interface AiService {
    Flux<String> callToNewChat(String userRequest, Student student);
    Flux<String> callByChatId(String userRequest, Long chatId);
}
