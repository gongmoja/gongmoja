package com.est.gongmoja.dto.chat;

import com.est.gongmoja.entity.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {

    private long id;
    private String title;

    public static ChatRoomResponseDto fromEntity(ChatRoomEntity chatRoomEntity) {
        ChatRoomResponseDto chatRoomDto = new ChatRoomResponseDto();
        chatRoomDto.setId(chatRoomEntity.getId());
        chatRoomDto.setTitle(chatRoomEntity.getTitle());
        return chatRoomDto;
    }


}