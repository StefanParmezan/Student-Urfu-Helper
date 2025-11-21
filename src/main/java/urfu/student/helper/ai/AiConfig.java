package urfu.student.helper.ai;

import chat.giga.springai.GigaChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import urfu.student.helper.ai.functions.BasicFunctions;
import urfu.student.helper.ai.memody.DbChatMemory;

import java.util.Objects;

@Configuration
public class AiConfig {
    public static final String SYS_PROMPT_FILE_NAME = "/system_prompt.txt";
    private final Resource systemPrompt = new InputStreamResource(
            Objects.requireNonNull(this.getClass().getResourceAsStream(SYS_PROMPT_FILE_NAME))
    );

    @Bean
    public ChatClient client(GigaChatModel model, BasicFunctions functions, DbChatMemory memory) {
        return ChatClient.builder(model)
                .defaultSystem(systemPrompt)
                .defaultTools(functions)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }
}
