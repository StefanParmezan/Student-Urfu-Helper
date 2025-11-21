package urfu.student.helper.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import urfu.student.helper.ai.AiService;

@AllArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final AiService aiService;

    public Flux<String> call(String message) {
        return aiService.call(message); //todo КАК ПОЛУЧИТЬ ПОЛЬЗОВАТЕЛЯ
    }
}
