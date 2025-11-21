package urfu.student.helper.services.chat;

import urfu.student.helper.models.chat.ChatEntity;

import java.util.List;

public interface ChatService {
    ChatEntity save(ChatEntity chat);
    List<ChatEntity> getByStudent(Long userId);
}
