package urfu.student.helper.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.chat.ChatService;
import urfu.student.helper.db.chat.dto.ChatPreviewDTO;
import urfu.student.helper.db.student.StudentService;
import urfu.student.helper.security.dto.StudentAuthentificationPrincipal;

import java.util.List;

@RestController("/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final ChatService chatService;

    @GetMapping("/chats")
    public Mono<List<ChatPreviewDTO>> getChats(@AuthenticationPrincipal StudentAuthentificationPrincipal student) {
        return chatService.getByStudent(studentService.get(student.getId())).collectList();
    }
}
