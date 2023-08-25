package com.est.gongmoja.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDataDto {
    private long id;

    public enum MessageType{
        ENTER, TALK
    }
    private MessageType type;
    private Long chatRoomId;
    private String sender;
//    private LocalDateTime sentTime;
    private String message;
//    private long userId;


}
