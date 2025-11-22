package urfu.student.helper.db.chat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.chat.dto.ChatDTO;
import urfu.student.helper.db.chat.dto.ChatPreviewDTO;
import urfu.student.helper.db.student.StudentEntity;

public interface ChatService {
    Mono<ChatEntity> get(Long id);
    Mono<ChatDTO> getDto(Long id);
    void delete(Long id);
    Flux<ChatPreviewDTO> getByStudent(StudentEntity student);
    Mono<ChatEntity> create(StudentEntity studentEntity);
    Mono<ChatEntity> save(ChatEntity chat);
    Flux<ChatEntity> getByStudent(Long ownerId);
}
