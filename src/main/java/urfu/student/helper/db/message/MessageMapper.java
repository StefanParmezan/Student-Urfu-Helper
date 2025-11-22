package urfu.student.helper.db.message;

import lombok.NonNull;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import urfu.student.helper.db.chat.ChatEntity;

import static org.springframework.ai.chat.messages.MessageType.TOOL;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {

    MessageEntity toEntity(Message message, ChatEntity chat);

    default Message toAiMessage(@NonNull MessageEntity entity) {
        return switch (entity.getType()) {
            case ASSISTANT -> new AssistantMessage(entity.getText());
            case SYSTEM -> new SystemMessage(entity.getText());
            case USER -> new UserMessage(entity.getText());
            case TOOL -> throw new UnsupportedOperationException(TOOL + " type is not supported yet");
        };
    }
}
