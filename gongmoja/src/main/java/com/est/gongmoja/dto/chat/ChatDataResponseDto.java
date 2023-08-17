package com.est.gongmoja.dto.chat;

import java.time.LocalDateTime;

public class ChatDataResponseDto {
    private long id;
    private LocalDateTime sentTime;
    private String content;
    private long userId;
    private long chatRoomId;
}
