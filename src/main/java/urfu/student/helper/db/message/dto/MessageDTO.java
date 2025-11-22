package urfu.student.helper.db.message.dto;

import org.springframework.ai.chat.messages.MessageType;

public record MessageDTO(
        String text,
        MessageType type
) {}
