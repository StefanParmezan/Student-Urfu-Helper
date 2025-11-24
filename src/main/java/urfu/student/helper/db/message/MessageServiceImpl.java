package urfu.student.helper.db.message;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import urfu.student.helper.db.chat.ChatService;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    private final ChatService chatService;
    private final MessageMapper mapper;

    @Override
    @Transactional
    public void save(Long chatId, List<Message> messages) {
        chatService.get(chatId)
                .subscribe(chat ->
                                repository.saveAll(
                                    messages.stream()
                                    .map(message -> mapper.toEntity(message, chat))
                                    .toList()
                                )
                );
    }

    @Override
    public List<Message> getAsSpringAiMessages(Long chatId) {
        return repository.findByChat_Id(chatId)
                .stream()
                .map(mapper::toAiMessage)
                .toList();
    }

    @Override
    public MessageEntity save(MessageEntity message) {
        return repository.save(message);
    }

    @Override
    public List<MessageEntity> getByChat(Long chatId) {
        return repository.findByChat_Id(chatId);
    }
}
