package urfu.student.helper.db.chat.dto;

import urfu.student.helper.db.message.dto.MessageDTO;

import java.util.List;

public record ChatDTO(
        Long id,
        List<MessageDTO> lastMessage
) {
}
