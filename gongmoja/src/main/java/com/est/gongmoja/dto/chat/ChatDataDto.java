package com.est.gongmoja.dto.chat;

import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDataDto {

    //    private ChatDataEntity.MessageType type;
    private Long chatRoomId;

    private Long senderId;
    private String sender;
    private String message;
    private String sentTime;

    private MessageType type;
//    private LocalDateTime sentTime;
//    private long userId;


}