package urfu.student.helper.services.chat;

import urfu.student.helper.models.chat.ChatEntity;
import urfu.student.helper.models.student.Student;

import java.util.List;

public interface ChatService {
    ChatEntity create(Student student);
    ChatEntity save(ChatEntity chat);
    List<ChatEntity> getByStudent(Long userId);
}
