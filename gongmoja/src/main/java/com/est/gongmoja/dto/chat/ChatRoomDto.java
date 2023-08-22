package com.est.gongmoja.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private Long id;

    private String title;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
//    private Long userCount;
//    private StockEntity stockId;
//    private List<ChatDataEntity> chatDataEntityList;
//    private List<UserEntity> chatUserList;
//    private Set<WebSocketSession> sessions = new HashSet<>();
//    public void handlerActions(WebSocketSession session, ChatService chatService, ChatDataEntity chatData)  {
//        if (chatData.isEntered() == false) {
//            sessions.add(session);
//            chatData.setEntered(true);
//            chatData.setMessage(chatData.getUser().getNickName() + "님이 입장했습니다.");
//        }
//        sendMessage(chatData, chatService);
//
//    }


}
