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

}
