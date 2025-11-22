package urfu.student.helper.db.chat;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import urfu.student.helper.db.student.StudentEntity;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository repository;

    @Override
    public ChatEntity get(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public ChatEntity create(StudentEntity studentEntity) {
        return repository.save(new ChatEntity(studentEntity));
    }

    @Override
    @Transactional
    public ChatEntity save(ChatEntity chat) {
        return repository.save(chat);
    }

    @Override
    @Transactional
    public List<ChatEntity> getByStudent(Long ownerId) {
        return repository.findAllByOwner_Id(ownerId);
    }
}
