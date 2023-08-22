package com.est.gongmoja.dto.chat;

import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.UserRepository;
import com.est.gongmoja.service.ChatService;
import com.est.gongmoja.service.UserService;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;

    private String title;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private Long userCount;
    private StockEntity stockId;
    private List<ChatDataEntity> chatDataEntityList;
    private List<UserEntity> chatUserList;
//    private Set<WebSocketSession> sessions = new HashSet<>();
//    public static void handlerActions(WebSocketSession session, ChatService chatService, ChatDataEntity chatData)  {
//        if (chatData.isEntered() == false) {
//            sessions.add(session);
//            chatData.setMessage(chatData.getUser().getNickName() + "님이 입장했습니다.");
//        }
//        sendMessage(chatData, chatService);
//
//    }
//
//    private void sendMessage(ChatDataEntity message, ChatService chatService) {
//        sessions.parallelStream()
//                .forEach(session -> chatService.convertMessage(session, message));
//
//    }
}
