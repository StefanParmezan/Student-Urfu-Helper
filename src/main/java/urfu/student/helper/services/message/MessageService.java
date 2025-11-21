package urfu.student.helper.services.message;

import org.springframework.ai.chat.messages.Message;
import urfu.student.helper.models.message.MessageEntity;

import java.util.List;

public interface MessageService {
    void save(Long chatId, List<Message> messages);
    List<Message> getAsSpringAiMessages(Long chatId);
    MessageEntity save(MessageEntity message);
    List<MessageEntity> getByChat(Long chatId);
}
