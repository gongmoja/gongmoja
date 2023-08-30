package com.est.gongmoja.controller;

import com.est.gongmoja.dto.StockDto;
import com.est.gongmoja.dto.chat.ChatDataDto;
import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.dto.user.UserDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.service.ChatService;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.NestingKind;
import java.net.Authenticator;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping
    public ChatRoomEntity createRoom(@RequestBody StockDto dto) {
        return chatService.createChatRoom(dto.getName());

    }

//    @GetMapping
//    public List<ChatRoomDto> findAllRoom() {
//        return chatService.findAllRoom();
//    }

    @GetMapping("/{chatRoomId}")
    public String redirectToChatRoom(@PathVariable Long chatRoomId) {
        // chatRoomId를 이용하여 URL 생성
        String chatRoomUrl = "/chat/" + chatRoomId; // 예시 URL
        // 리다이렉트
        return "redirect:" + chatRoomUrl;
    }

    @PostMapping("/{chatRoomId}")
    public void createChatDataAndDTO(@PathVariable Long chatRoomId,
                               Authentication authentication,
                               String message
//                                ,UserDto userDto
    ){

        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        ChatDataEntity chatData = chatService.keepMessage(message, userEntity, chatRoomId);

        ChatDataDto dto = chatService.entityToDto(chatData);
        sendMessage(dto);
    }



    @MessageMapping("/chat")
    public void sendMessage(ChatDataDto chatData){
//        chatService.sendMessage(chatData);


        if(ChatDataEntity.MessageType.ENTER.equals(chatData.getType())){
            //현재 시간을 가져오는 줄 필요
            chatData.setMessage(chatData.getSender() + "님이 입장하셨습니다.");
        }
        messagingTemplate.convertAndSend("/sub/chatroom/" + chatData.getChatRoomId(), chatData);
    }
}
