package urfu.student.helper.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import urfu.student.helper.ai.AiService;

@AllArgsConstructor
@RestController
@RequestMapping("/chat/message")
public class MessageController {
    private final AiService aiService;

    @PostMapping("/send")
    public Flux<String> call(String message, Long chatId) {
        return aiService.callByChatId(message, chatId);
        //TODO реалоизовать вызов на новый чат
    }
}
