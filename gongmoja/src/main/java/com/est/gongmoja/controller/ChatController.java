package com.est.gongmoja.controller;

import com.est.gongmoja.dto.chat.ChatDataDto;
import com.est.gongmoja.dto.chat.ChatRoomResponseDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.UserRepository;
import com.est.gongmoja.service.ChatService;
import com.est.gongmoja.service.StockService;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;
    private final UserService userService;
    private final StockService stockService;


    private final UserRepository userRepository;

    @GetMapping("/{chatRoomId}")
    public String joinChatRoom(@PathVariable Long chatRoomId, Model model, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());

        // chatService를 사용하여 주식 이름 가져오기
        StockEntity stock = stockService.findStockById(chatRoomId);

        if (stock == null) {
            throw new CustomException(ErrorCode.STOCK_NOT_FOUND);
        }
        List<ChatDataEntity> chatMessages = chatService.getMessagesByChatRoomId(chatRoomId);
        LocalDateTime localDateTime = stock.getEndDate();
        String endDateTimeStr = localDateTime.toString();


        log.info(endDateTimeStr);
        model.addAttribute("endDate", endDateTimeStr);
        model.addAttribute("chatRoomId", chatRoomId);
        model.addAttribute("stockName", stock.getName());
        model.addAttribute("chatMessages", chatMessages);
        model.addAttribute("currentUserId", userEntity.getId());
        model.addAttribute("userEntity", userEntity); // top bar에서 이용

        return "chat/chat-room";
    }


    @GetMapping("/list")
    public String showChatRoomList(Model model) {
        List<ChatRoomResponseDto> chatRooms = chatService.getAllChatRooms();
        model.addAttribute("chatRooms", chatRooms);
        return "chat/list";
    }

    @MessageMapping("/chat")
    @Transactional
    public void sendChat(ChatDataDto chatData, Authentication authentication){
        UserEntity user = (UserEntity)authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        chatData.setSender(userEntity.getNickName());
        ChatRoomEntity chatRoom = chatService.findRoomById(chatData.getChatRoomId());
        Optional<UserEntity> optionalUser =
                userRepository.findByIdAndChatRooms(userEntity.getId(),chatRoom);
        if(optionalUser.isEmpty()){
            userEntity.getChatRooms().add(chatRoom);
            userService.saveUser(userEntity);
        }
        chatService.sendChat(chatData);
    }

    @MessageMapping("/date")
    @Transactional
    public void sendDate(ChatDataDto chatData){
        chatService.sendChat(chatData);
    }

}
