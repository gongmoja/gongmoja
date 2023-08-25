package com.est.gongmoja.controller;

import com.est.gongmoja.dto.StockDto;
import com.est.gongmoja.dto.chat.ChatDataDto;
import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.NestingKind;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping
    public ChatRoomEntity createRoom(@RequestBody StockDto dto) {
        return chatService.createChatRoom(dto.getName());

    }

    @GetMapping
    public List<ChatRoomDto> findAllRoom() {
        return chatService.findAllRoom();
    }

    @MessageMapping("/chat")
    public void sendMessage(ChatDataDto chatData){
//        chatService.sendMessage(chatData);
        if(ChatDataDto.MessageType.ENTER.equals(chatData.getType())){
            //현재 시간을 가져오는 줄 필요
            chatData.setMessage(chatData.getSender() + "님이 입장하셨습니다.");
        }
        messagingTemplate.convertAndSend("/sub/chatroom/" + chatData.getChatRoomId(), chatData);
    }
}
