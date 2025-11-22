package urfu.student.helper.ai;

import reactor.core.publisher.Flux;
import urfu.student.helper.models.student.StudentEntity;

public interface AiService {
    Flux<String> callToNewChat(String userRequest, StudentEntity studentEntity);
    Flux<String> callByChatId(String userRequest, Long chatId);
}
