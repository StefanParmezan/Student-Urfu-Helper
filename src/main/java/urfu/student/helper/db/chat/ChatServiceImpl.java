package urfu.student.helper.db.chat;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import urfu.student.helper.db.chat.dto.ChatDTO;
import urfu.student.helper.db.chat.dto.ChatPreviewDTO;
import urfu.student.helper.db.student.StudentEntity;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository repository;
    private final ChatMapper mapper;

    @Override
    @Transactional
    public Mono<ChatEntity> get(Long id) {
        return Mono.fromCallable(() -> repository.findById(id).orElseThrow());
    }

    @Override
    @Transactional
    public Mono<ChatDTO> getDto(Long id) {
        return get(id).map(mapper::toDto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        get(id).subscribe(repository::delete);
    }

    @Override
    @Transactional
    public Flux<ChatPreviewDTO> getByStudent(StudentEntity student) {
        return Flux.fromIterable(repository.findAllByOwner_Id(student.getId()))
                .map(mapper::toPreviewDto);
    }

    @Override
    @Transactional
    public Mono<ChatEntity> create(StudentEntity studentEntity) {
        return Mono.fromCallable(() -> repository.save(new ChatEntity(studentEntity)));
    }

    @Override
    @Transactional
    public Mono<ChatEntity> save(ChatEntity chat) {
        return Mono.fromCallable(() ->  repository.save(chat));
    }

    @Override
    @Transactional
    public Flux<ChatEntity> getByStudent(Long ownerId) {
        return Flux.fromIterable(repository.findAllByOwner_Id(ownerId));
    }
}
