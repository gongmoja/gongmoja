package com.est.gongmoja.config;

import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

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

//        chatRoom.handleActions(session, chatMessage, chatService);
    }
}
