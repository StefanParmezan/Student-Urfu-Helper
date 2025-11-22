package urfu.student.helper.ai;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.publisher.Flux;
import urfu.student.helper.models.chat.ChatEntity;
import urfu.student.helper.models.student.Student;
import urfu.student.helper.services.chat.ChatService;

import java.util.function.Consumer;

@AllArgsConstructor
public class AiServiceImpl implements AiService {
    private final ChatService chatService;
    private final ChatClient client;

    @Override
    public Flux<String> callToNewChat(String input, Student student) {
        ChatEntity chat = chatService.create(student);
        return callByChatId(input, chat.getId());
    }

    @Override
    public Flux<String> callByChatId(String input, Long chatId) {
        return client.prompt(input)
                .advisors(new ConversationIdSetter(chatId))
                .stream()
                .content();
    }

    private record ConversationIdSetter(Long id) implements Consumer<ChatClient.AdvisorSpec> {

        @Override
        public void accept(ChatClient.AdvisorSpec advisorSpec) {
            advisorSpec.param(ChatMemory.CONVERSATION_ID, id);
        }
    }
}
