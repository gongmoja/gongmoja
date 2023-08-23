package com.est.gongmoja.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatDataDto {
    private long id;

    public enum messageType{
        ENTER, TALK
    }
    private messageType type;
    private String chatRoomId;
    private String sender;
//    private LocalDateTime sentTime;
    private String content;
//    private long userId;


}
