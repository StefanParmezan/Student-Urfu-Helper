package urfu.student.helper.db.message;

import org.springframework.ai.chat.messages.Message;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

public class MessageServiceImpl implements MessageService {
    @Override
    public void save(Long chatId, List<Message> messages) {

    }

    @Override
    public List<Message> getAsSpringAiMessages(Long chatId) {
        return List.of();
    }

    @Override
    public MessageEntity save(MessageEntity message) {
        return null;
    }

    @Override
    public List<MessageEntity> getByChat(Long chatId) {
        return List.of();
    }
}
