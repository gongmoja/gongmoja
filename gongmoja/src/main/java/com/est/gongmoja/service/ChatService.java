package com.est.gongmoja.service;

import com.est.gongmoja.dto.chat.ChatDataDto;
import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.dto.chat.ChatRoomResponseDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.ChatDataRepository;
import com.est.gongmoja.repository.ChatRoomRepository;
import com.est.gongmoja.repository.StockRepository;
import com.est.gongmoja.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.Authenticator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatDataRepository chatDataRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    //채팅방 별 연결된 웹소캣 세션의 집합
    private Map<String, ChatRoomDto> chatRooms = new LinkedHashMap<>();
    private Set<WebSocketSession> sessions = new HashSet<>();

    private final ObjectMapper objectMapper;




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
                    .userCount(0L)
                    .build();
            chatRoomRepository.save(chatRoom);
        }
    }

//    public List<ChatRoomEntity> findAllRoom() {
//        return new ArrayList<>(chatRooms.values());
//    }

//    public ChatDataEntity keepMessage(String content,
//                                      UserEntity user,
//                                      Long chatRoomId
////            , ChatRoomEntity chatRoom
//    ) {
//        Optional<ChatRoomEntity> optionalChatRoomEntity = chatRoomRepository.findById(chatRoomId);
//        if(optionalChatRoomEntity.isEmpty()){
//            throw new CustomException(ErrorCode.CHATROOM_NOT_FOUND);
//        }
//        ChatRoomEntity chatRoom = optionalChatRoomEntity.get();
//
//        Optional<ChatDataEntity> optionalChatDataEntity = chatDataRepository.findByUser(user);
//        ChatDataEntity.MessageType messageType = optionalChatDataEntity.isEmpty() ?
//                ChatDataEntity.MessageType.ENTER : ChatDataEntity.MessageType.TALK;
//
//        ChatDataEntity chatData = ChatDataEntity.builder()
//                .type(messageType)
//                .message(content)
//                .createdAt(LocalDateTime.now())
//                .user(user)
//                .chatRoom(chatRoom)
//                .build();
//        chatDataRepository.save(chatData);
//        return chatData;
//
//    }

//    public ChatDataDto entityToDto(ChatDataEntity entity){
//        ChatDataDto dto = new ChatDataDto();
//        dto.setType(entity.getType());
//        dto.setMessage(entity.getMessage());
//        dto.setSentTime(entity.getCreatedAt());
//        dto.setSender(entity.getUser().getNickName());
//
//        return dto;
//    }



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


//    /**
//     * 채팅방 입장
//     * <p>
//     * 입장하려는 채팅방이 존재하고, 그 채팅방이 공모주에 연결되어 있고, 해당 공모주를 즐겨찾기했는지 확인한다
//     *
//     * @param chatDataDto 해당 채팅방의 정보
//     */
//    public void joinChat(ChatDataDto chatDataDto) {
//
//
//        //채팅방 존재 유무 확인
//        Optional<ChatRoomEntity> chatRoomEntityOptional = chatRoomRepository.findById(chatDataDto.getChatRoomId());
//        ChatRoomEntity chatRoom;
//        if (chatRoomEntityOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다");
//        } else {
//            chatRoom = chatRoomEntityOptional.get();
//        }
//
//        //공모주 존재여부 확인
//        Optional<StockEntity> stockEntityOptional = stockRepository.findById(chatRoom.getStockId().getId());
//        StockEntity stock;
//        if (stockEntityOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공모주가 존재하지 않습니다");
//        } else {
//            stock = stockEntityOptional.get();
//        }
//
//        //발신 사용자 존재여부
//        Optional<UserEntity> userEntityOptional = userRepository.findByUserName(chatDataDto.getSender());
//        UserEntity user = null;
//        if (userEntityOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공모주가 존재하지 않습니다");
//        } else {
//            user = userEntityOptional.get();
//        }
//
//        //즐겨찾기 여부 확인
//        if (user.getStocks().contains(stock) == false) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기가 되어있지 않은 공모주입니다");
//        }
//        //유저 추가 엔티티에 매핑 건의
//        //기본메시지 강제 발송
//        chatRoom.setUserCount(chatRoom.getUserCount() + 1);
//    }
//
//    public void sendMessage(ChatDataDto chatData){
//        if(ChatDataEntity.MessageType.ENTER.equals(chatData.getType())){
//            //현재 시간을 가져오는 줄 필요
//            chatData.setMessage(chatData.getSender() + "님이 입장하셨습니다.");
//        }
//        messagingTemplate.convertAndSend("/sub/chatrrom/" + chatData.getChatRoomId(), chatData);
//    }

    public List<ChatRoomResponseDto> getAllChatRooms() {
        List<ChatRoomEntity> chatRooms = chatRoomRepository.findAll(); // ChatRoomRepository는 DB에서 채팅방 정보를 가져오는 역할
        return chatRooms.stream()
                .map(ChatRoomResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public void saveChatMessage(ChatDataDto chatMessage) {
        ChatDataEntity chatData = ChatDataEntity.builder()
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getSentTime())
//                .user(chatMessage.getSender())
//                .chatRoom(chatMessage.getChatRoomId())
                .build();
    }

    public boolean isFavorite(UserEntity userEntity, Long chatRoomId) {
        Optional<UserEntity> optionalUser = userRepository.findById(userEntity.getId());

        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        }

        UserEntity user = optionalUser.get();
        List<StockEntity> favoriteStocks = user.getStocks();

        // 주어진 chatRoomId를 가진 주식이 favoriteStocks에 있는지 확인
        boolean isFavorite = favoriteStocks.stream()
                .anyMatch(stock -> stock.getId().equals(chatRoomId));

        return isFavorite;
    }


//    public boolean isFavorite(UserEntity userEntity, Long id) {
//        Optional<StockEntity> optionalStock = userEntity.getStocks().;
//        return optionalStock.isPresent();
//    }
}


