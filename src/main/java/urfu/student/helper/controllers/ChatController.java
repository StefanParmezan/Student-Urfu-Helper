package urfu.student.helper.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import urfu.student.helper.ai.AiService;
import urfu.student.helper.models.student.StudentEntity;

@AllArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final AiService aiService;

    @PostMapping("/{chatId}")
    public Flux<String> call(String message, Long chatId) {
        return aiService.callByChatId(message, chatId);
    }

    /*@PostMapping("/new")
    public Flux<String> call(String message) {
        StudentEntity studentEntity; //TODO Получить всё таки студента
        return aiService.callToNewChat(message, studentEntity);
    }*/
}
