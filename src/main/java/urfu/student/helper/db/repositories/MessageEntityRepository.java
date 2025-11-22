package urfu.student.helper.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import urfu.student.helper.db.message.MessageEntity;

public interface MessageEntityRepository extends JpaRepository<MessageEntity, Long> {
    MessageEntity save(MessageEntity message);
}