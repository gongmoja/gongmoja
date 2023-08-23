package com.est.gongmoja.dto.chat;

import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.UserRepository;
import com.est.gongmoja.service.ChatService;
import lombok.*;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDto {
    private String id;

    private String title;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private Long userCount;
    private StockEntity stockId;
    private List<ChatDataEntity> chatDataEntityList;
    private List<UserEntity> chatUserList;

    private Set<WebSocketSession> sessions;
    public void handlerActions(WebSocketSession session, ChatService chatService, ChatDataDto chatData)  {
        if (this.sessions == null) {
            this.sessions = new HashSet<>(); // Initialize the sessions if it's null
        }
        if (chatData.getType().equals(ChatDataDto.messageType.ENTER)) {
            sessions.add(session);
//            chatData.setContent(userRepository.findById(chatData.getUserId()).get().getNickName() + "님이 입장했습니다.");
            chatData.setContent(chatData.getSender() + "님이 입장했습니다.");
        }
        convertMessage(chatData, chatService);

    }

    public <T> void convertMessage(T message, ChatService chatService) {
        sessions.parallelStream().forEach(session -> chatService.sendMessageToAll(session, message));
    }


}
