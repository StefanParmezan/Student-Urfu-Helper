package urfu.student.helper.db.message;

import org.springframework.ai.chat.messages.Message;

import java.util.List;

public interface MessageService {
    void save(Long chatId, List<Message> messages);
    List<Message> getAsSpringAiMessages(Long chatId);
    MessageEntity save(MessageEntity message);
    List<MessageEntity> getByChat(Long chatId);
}
