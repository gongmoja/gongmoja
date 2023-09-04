package com.est.gongmoja.controller;

import com.est.gongmoja.dto.StockDto;
import com.est.gongmoja.dto.chat.ChatDataDto;
import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.dto.chat.ChatRoomResponseDto;
import com.est.gongmoja.dto.user.UserDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.service.ChatService;
import com.est.gongmoja.service.StockService;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.lang.model.element.NestingKind;
import java.net.Authenticator;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final StockService stockService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final SimpMessagingTemplate simpMessagingTemplate;

//    @PostMapping
//    public ChatRoomEntity createRoom(@RequestBody StockDto dto) {
//        return chatService.createChatRoom(dto.getName());
//
//    }

//    @GetMapping("/list")
//    public List<ChatRoomDto> findAllRoom() {
//        return chatService.findAllRoom();
//    }

    @GetMapping("/{chatRoomId}")
    public String joinChatRoom(@PathVariable Long chatRoomId, Model model, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());

        // chatService를 사용하여 주식 이름 가져오기
        String stockName = stockService.findStockNameById(chatRoomId);

        if (stockName == null) {
            throw new CustomException(ErrorCode.STOCK_NOT_FOUND);
        }

        // 주식 이름을 모델에 추가
        model.addAttribute("chatRoomId", chatRoomId);
        model.addAttribute("stockName", stockName);

        if (!chatService.isFavorite(userEntity, chatRoomId)) {
            throw new CustomException(ErrorCode.NOT_FAVORITE_STOCK);
        }

        return "chat/chat-room";
    }


    @GetMapping("/list")
    public String showChatRoomList(Model model) {
        List<ChatRoomResponseDto> chatRooms = chatService.getAllChatRooms();
        model.addAttribute("chatRooms", chatRooms);
        return "chat/list";
    }

//    @GetMapping("rooms/{id}/name")
//    public ResponseEntity<ChatRoomEntity> getRoomName(@PathVariable("id") Long roomId) {
//        return ResponseEntity.ok(chatService.findRoomById(roomId));
//    }
//
//    @GetMapping("{chatRoomId}/{userId}")
//    public String enterRoom(){
//        return "chat/chat-room";
//    }

//    @MessageMapping("/chat")
//    public void sendChat(
//            @Payload ChatDataDto chatMessage,
//            // STOMP over WebSocket은 Header를 포함할 수 있다
//            @Headers Map<String, Object> headers,
//            @Header("nativeHeaders") Map<String, String> nativeHeaders
//    ){
//        log.info(chatMessage.toString());
//        log.info(headers.toString());
//        log.info(nativeHeaders.toString());
//        String time = new SimpleDateFormat("HH:mm").format(new Date());
//        chatMessage.setSentTime(time);
//        chatService.saveChatMessage(chatMessage);
//        simpMessagingTemplate.convertAndSend(
//                String.format("/topic/%s", chatMessage.getChatRoomId()),
//                chatMessage
//        );
//    }


    @MessageMapping("/chat")
    public void sendChat(ChatDataDto chatData, Authentication authentication){
        UserEntity user = (UserEntity)authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        chatData.setSender(userEntity.getNickName());
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        chatData.setSentTime(time);
        log.info(chatData.getSender());
        log.info(chatData.getSentTime());
        log.info(String.valueOf(chatData.getChatRoomId()));
        log.info(chatData.getMessage());
        simpMessagingTemplate.convertAndSend(String.format("/topic/%s", chatData.getChatRoomId()), chatData);
    }
    @MessageMapping("/chatroom")
    public void testSend(String data){
        log.info("전송요청 받음");

//        log.info("전송요청 받음" + chatData.getMessage());
//        if(ChatDataEntity.MessageType.ENTER.equals(chatData.getType())){
//            //현재 시간을 가져오는 줄 필요
//            chatData.setMessage(chatData.getSender() + "님이 입장하셨습니다.");
//        }
        messagingTemplate.convertAndSend("/sub/chatroom/1", data);
    }
}
