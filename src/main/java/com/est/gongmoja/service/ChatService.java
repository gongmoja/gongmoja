package com.est.gongmoja.service;

import com.est.gongmoja.dto.chat.ChatDataDto;
import com.est.gongmoja.dto.chat.ChatRoomResponseDto;
import com.est.gongmoja.entity.*;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.ChatDataRepository;
import com.est.gongmoja.repository.ChatRoomRepository;
import com.est.gongmoja.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatDataRepository chatDataRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;


    public void createChatRoomForStock(StockEntity stock) {
        // 주식id를 기반으로 채팅방 이름 생성
        String chatRoomName = stock.getName() + " 채팅방";

        // 이미 채팅방이 있는지 확인
        Optional<ChatRoomEntity> existingChatRoom = chatRoomRepository.findByTitle(chatRoomName);
        if (existingChatRoom.isEmpty()) {
            ChatRoomEntity chatRoom =ChatRoomEntity.builder()
                    .title(chatRoomName)
                    .openDate(stock.getStartDate())
                    .closeDate(stock.getEndDate())
                    .stockId(stock)
                    .build();
            chatRoomRepository.save(chatRoom);
        }
    }

    public void sendChat(ChatDataDto chatData){
//        String time = new SimpleDateFormat("HH:mm").format(new Date());
//        chatData.setSentTime(time);
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime koreaTime = ZonedDateTime.now(koreaZone);
        LocalDateTime createdAt = koreaTime.toLocalDateTime();
        Optional<ChatRoomEntity> optionalChatRoomEntity = chatRoomRepository.findById(chatData.getChatRoomId());
        ChatRoomEntity chatRoom = optionalChatRoomEntity.get();
        Optional<UserEntity> optionalUser = userRepository.findById(chatData.getSenderId());
        UserEntity user = optionalUser.get();
        ChatDataEntity chatDataEntity = ChatDataEntity.builder()
                .message(chatData.getMessage())
                .createdAt(createdAt)
                .user(user)
                .chatRoom(chatRoom)
                .type(chatData.getType())
                .build();
        chatDataRepository.save(chatDataEntity);


//        log.info(chatData.getSender());
//        log.info(chatData.getSentTime());
//        log.info(String.valueOf(chatData.getChatRoomId()));
//        log.info(chatData.getMessage());
        simpMessagingTemplate.convertAndSend(String.format("/topic/%s", chatData.getChatRoomId()), chatData);
    }

    public void sendDate(ChatDataDto chatData){
        UserEntity admin = userRepository.findById(1L).get();
        //한국시간으로 변경
        ZoneId koreaZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime koreaTime = ZonedDateTime.now(koreaZone);
        LocalDateTime createdAt = koreaTime.toLocalDateTime();
        Optional<ChatRoomEntity> optionalChatRoomEntity = chatRoomRepository.findById(chatData.getChatRoomId());
        ChatRoomEntity chatRoom = optionalChatRoomEntity.get();
        ChatDataEntity chatDataEntity = ChatDataEntity.builder()
                .message(chatData.getMessage())
                .createdAt(createdAt)
                .user(admin)
                .chatRoom(chatRoom)
                .type(chatData.getType())
                .build();
        chatDataRepository.save(chatDataEntity);
        simpMessagingTemplate.convertAndSend(String.format("/topic/%s", chatData.getChatRoomId()), chatData);
    }

    /**
     * 체팅방을 해방 채팅방의 id로 찾는다
     *
     * @param id 채팅방의 id
     * @return 해당 id에 일치하는 채팅방 객체
     */
    public ChatRoomEntity findRoomById(Long id) {
        Optional<ChatRoomEntity> optionalChatRoomEntity = chatRoomRepository.findById(id);
        if(optionalChatRoomEntity.isEmpty()){
            throw new CustomException(ErrorCode.CHATROOM_NOT_FOUND);
        }
        return optionalChatRoomEntity.get();

    }


    public List<ChatRoomEntity> getAllChatRooms() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        return chatRoomRepository.findAll(sort);
    }


    public List<ChatDataEntity> getMessagesByChatRoomId(Long chatRoomId) {
        return chatDataRepository.findByChatRoom_IdOrderByIdAsc(chatRoomId);
    }


    public boolean shouldSendDateMessage(Long chatRoomId) {
        List<ChatDataEntity> messages =  getMessagesByChatRoomId(chatRoomId);

        LocalDate today = LocalDate.now();
        // 메시지를 하나씩 확인하면서 오늘 일의 날짜를 가지고 있는지 확인
        for (ChatDataEntity message : messages) {
            LocalDateTime createdAt = message.getCreatedAt();
            LocalDate messageDate = createdAt.toLocalDate();

            if (messageDate.equals(today)) {
                // 오늘 일의 날짜를 가지고 있는 메시지가 하나라도 있다면 false 반환
                return false;
            }
        }
        return true;
    }

    public List<ChatRoomEntity> getUserChatRooms(UserEntity userEntity) {
        List<ChatRoomEntity> chatRooms = userEntity.getChatRooms();

        // ChatRoomEntity를 id 역순으로 정렬
        Comparator<ChatRoomEntity> byIdDescending = Comparator.comparing(ChatRoomEntity::getId).reversed();

        // 정렬된 List 반환
        return chatRooms.stream()
                .sorted(byIdDescending)
                .collect(Collectors.toList());
    }
    public Page<ChatRoomEntity> getAllChatRoomsPaged(Pageable pageable) {
        return chatRoomRepository.findAll(pageable);
    }

    public Page<ChatRoomEntity> getUserChatRoomsPaged(UserEntity userEntity, Pageable pageable) {
        return chatRoomRepository.findByUsers(userEntity, pageable);
    }

    public void removeUserFromChatRoom(UserEntity userEntity, Long chatRoomId) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
        if (userEntity != null && chatRoom != null) {
            // ManyToMany 관계를 해제합니다.
            userEntity.getChatRooms().remove(chatRoom);
            userService.saveUser(userEntity);
//            chatRoom.getUsers().remove(userEntity);
        }


    }

    //    public boolean isFavorite(UserEntity userEntity, Long chatRoomId) {
//        Optional<UserEntity> optionalUser = userRepository.findById(userEntity.getId());
//
//        if (optionalUser.isEmpty()) {
//            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
//        }
//
//        UserEntity user = optionalUser.get();
//        List<StockEntity> favoriteStocks = user.getStocks();
//
//        // 주어진 chatRoomId를 가진 주식이 favoriteStocks에 있는지 확인
//        boolean isFavorite = favoriteStocks.stream()
//                .anyMatch(stock -> stock.getId().equals(chatRoomId));
//
//        return isFavorite;
//    }

}