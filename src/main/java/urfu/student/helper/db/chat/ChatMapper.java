package urfu.student.helper.db.chat;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import urfu.student.helper.db.chat.dto.ChatDTO;
import urfu.student.helper.db.chat.dto.ChatPreviewDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatMapper {
    ChatDTO toDto(ChatEntity entity);
    ChatPreviewDTO toPreviewDto(ChatEntity entity);
}
