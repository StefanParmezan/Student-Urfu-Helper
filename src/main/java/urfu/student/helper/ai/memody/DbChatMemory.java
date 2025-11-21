package urfu.student.helper.ai.memody;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import urfu.student.helper.services.message.MessageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DbChatMemory implements ChatMemory {
    private final MessageService messageService;

    @Override
    @Transactional
    public void add(@NonNull String conversationId, @NonNull List<Message> messages) {
        messageService.save(Long.parseLong(conversationId), messages);
    }

    @Override
    @NonNull
    @Transactional
    public List<Message> get(@NonNull String conversationId) {
        return messageService.getAsSpringAiMessages(Long.parseLong(conversationId));
    }

    @Override
    public void clear(@NonNull String conversationId) {
        throw new RuntimeException("not implemented");
    }
}