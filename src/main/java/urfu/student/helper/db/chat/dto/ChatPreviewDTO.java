package urfu.student.helper.db.chat.dto;

import urfu.student.helper.db.message.dto.MessageDTO;

public record ChatPreviewDTO(
        Long id,
        MessageDTO lastMessage
) {}