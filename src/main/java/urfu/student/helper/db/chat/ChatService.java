package urfu.student.helper.db.chat;

import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

public interface ChatService {
    ChatEntity get(Long id);
    ChatEntity create(StudentEntity studentEntity);
    ChatEntity save(ChatEntity chat);
    List<ChatEntity> getByStudent(Long ownerId);
}
