package urfu.student.helper.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import urfu.student.helper.ai.AiService;
import urfu.student.helper.db.chat.ChatService;
import urfu.student.helper.db.chat.dto.ChatDTO;
import urfu.student.helper.db.student.StudentService;
import urfu.student.helper.security.dto.StudentAuthentificationPrincipal;

@AllArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final AiService aiService;
    private final ChatService chatService;
    private final StudentService studentService;

    @GetMapping("/{chatId}")
    public Mono<ChatDTO> get(@PathVariable Long chatId) {
        return chatService.getDto(chatId);
    }

    @DeleteMapping("/{chatId}")
    public void delete(@PathVariable Long chatId) {
        chatService.delete(chatId);
    }

    @PostMapping(value = "/chat/new/message", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sendNewMessage(@RequestBody String request, @AuthenticationPrincipal StudentAuthentificationPrincipal student) {
        return aiService.callToNewChat(request, studentService.get(student.getId()));
    }

    @PostMapping(value = "/chat/{chatId}/message", produces=MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> sendNewMessage(@RequestBody String request, @PathVariable Long chatId) {
        return chatService.get(chatId)
                .flatMapMany(chat -> aiService.callByChatId(request, chat));
    }
}
