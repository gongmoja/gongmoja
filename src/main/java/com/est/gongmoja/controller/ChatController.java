package com.est.gongmoja.controller;

import com.est.gongmoja.dto.chat.ChatDataDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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



//    @GetMapping("/list")
//    public String showChatRoomList(Model model, Authentication authentication) {
//        UserEntity user = (UserEntity) authentication.getPrincipal();
//        UserEntity userEntity = userService.getUser(user.getUserName());
//
//        if (userEntity.getId() == 1) {
//            // id가 1인 경우 모든 채팅방을 가져올 로직을 수행
//            List<ChatRoomEntity> allChatRooms = chatService.getAllChatRooms();
//            model.addAttribute("userChatRooms", allChatRooms);
//        } else {
//            List<ChatRoomEntity> userChatRooms = chatService.getUserChatRooms(userEntity);
//            model.addAttribute("userChatRooms", userChatRooms);
//        }
//
//        model.addAttribute("userEntity", userEntity); // top bar에서 이용
//        return "chat/list";
//    }

    @GetMapping("/list")
    public String showChatRoomList(Model model, Authentication authentication,
                                   @RequestParam(name = "page", defaultValue = "0") int page) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

        if (userEntity.getId() == 1) {
            // id가 1인 경우 모든 채팅방을 가져올 로직을 수행
            Page<ChatRoomEntity> allChatRooms = chatService.getAllChatRoomsPaged(pageable);
            model.addAttribute("userChatRooms", allChatRooms);
        } else {
            Page<ChatRoomEntity> userChatRooms = chatService.getUserChatRoomsPaged(userEntity, pageable);
            model.addAttribute("userChatRooms", userChatRooms);
        }
        model.addAttribute("currentUserId", userEntity.getId());
        model.addAttribute("userEntity", userEntity); // top bar에서 이용
        return "chat/list";
    }


    @MessageMapping("/chat")
    @Transactional
    public void sendChat(ChatDataDto chatData, Authentication authentication){
        UserEntity user = (UserEntity)authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        chatData.setSender(userEntity.getNickName());
        chatData.setSenderId(userEntity.getId());
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
        log.info("메시지 매핑 호출 완료");
        chatService.sendDate(chatData);
    }


    @PostMapping("/date-check")
    @ResponseBody
    public Boolean dateCheck(@RequestParam("chatRoomId") Long chatRoomId) {
        log.info("포스트 호출 완료");
        boolean isValid = chatService.shouldSendDateMessage(chatRoomId);
        log.info(String.valueOf(isValid));
        return isValid;
    }

    @PostMapping("/delete-from/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<String> deleteUserFromChatRoom(Authentication authentication, @PathVariable("chatRoomId") Long chatRoomId){
        UserEntity user = (UserEntity)authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        chatService.removeUserFromChatRoom(userEntity,chatRoomId);
        return ResponseEntity.ok("채팅방을 나갔습니다.");

    }



}
