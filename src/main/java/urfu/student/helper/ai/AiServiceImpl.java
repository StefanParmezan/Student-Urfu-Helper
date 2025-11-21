package urfu.student.helper.ai;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import reactor.core.publisher.Flux;
import urfu.student.helper.models.student.Student;

import java.util.function.Consumer;

@AllArgsConstructor
public class AiServiceImpl implements AiService {
    private final ChatClient client;

    @Override
    public Flux<String> call(String input, Student user) {
        return client.prompt(input)
                .advisors(new ConversationIdSetter(user))
                .stream()
                .content();
    }

    private record ConversationIdSetter(Student user) implements Consumer<ChatClient.AdvisorSpec> {

        @Override
        public void accept(ChatClient.AdvisorSpec advisorSpec) {
            advisorSpec.param(ChatMemory.CONVERSATION_ID, user.getId());
        }
    }
}
