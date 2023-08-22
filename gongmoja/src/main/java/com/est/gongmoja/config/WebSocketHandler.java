package com.est.gongmoja.config;

import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.jwt.JwtTokenUtil;
import com.est.gongmoja.repository.UserRepository;
import com.est.gongmoja.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 클라이언트로부터 받은 메시지를 문자열 형태로 가져오기
        String payload = message.getPayload();
        // 받은 메시지를 로그로 출력
        log.info("Received message: {}", payload);

        // JSON 문자열을 ChatDataEntity로 변환
        ChatDataEntity chatData = objectMapper.readValue(payload, ChatDataEntity.class);

        // 메시지가 존재하는 방 탐색
        ChatRoomEntity chatRoom = chatService.findRoomById(chatData.getChatRoom().getId());

        chatService.handlerActions(session, chatRoom, chatData);


    }
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
////        request.getCookies();
////
//        String token = session.getUri().getQuery(); // JWT 토큰을 쿼리 파라미터로 받음
//        String username = jwtTokenUtil.getUsername(token);
//
//        if (username != null) {
//            Optional<UserEntity> optionalUserEntity = userRepository.findByUserName(username);
//            UserEntity userEntity = optionalUserEntity.get();
//            if (userEntity != null) {
//                // 즐겨찾기된 Stock에 연결된 채팅방에 입장
//                List<StockEntity> favoriteStocks = userEntity.getStocks();
//                for (StockEntity stock : favoriteStocks) {
//                    ChatRoomEntity chatRoom = stock.getChatRoom();
//                    chatService.addSessionToChatRoom(chatRoom.getId(), session);
//                    }
//                }
//            }
//        }
}
