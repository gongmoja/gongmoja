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
    private Long id;
    private String title;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private Long userCount;
    private StockEntity stockId;
    private List<ChatDataEntity> chatDataEntityList;
    private List<UserEntity> chatUserList;

}
