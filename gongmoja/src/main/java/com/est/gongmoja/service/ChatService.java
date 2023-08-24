package com.est.gongmoja.service;

import com.est.gongmoja.dto.chat.ChatDataDto;
import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
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
import java.util.*;

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


    public List<ChatRoomDto> findAllRoom(){
        return new ArrayList<>(chatRooms.values());
    }
//    public ChatRoomDto createRoom(String name) {
//        String randomId = UUID.randomUUID().toString();
//        ChatRoomDto chatRoom = ChatRoomDto.builder()
//                .id(randomId)
////                .title(chatRoomDto.getTitle())
//                .title(name)
////                .openDate(chatRoomDto.getOpenDate())
////                .closeDate(chatRoomDto.getCloseDate())
//                .build();
//        chatRooms.put(randomId, chatRoom);
//        return chatRoom;
//    }

    public ChatRoomEntity createChatRoom(String stockName){
        log.info(stockName);
        Optional<StockEntity> optionalStock = stockRepository.findByName(stockName);
        if(optionalStock.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공모주가 존재하지 않습니다");
        }
        StockEntity stock = optionalStock.get();
        ChatRoomEntity chatRoom =ChatRoomEntity.builder()
                .title(stockName + " 채팅방")
                .openDate(stock.getStartDate())
                .closeDate(stock.getEndDate())
                .userCount(0L)
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    public <T> void sendMessageToAll(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

//    public List<ChatRoomEntity> findAllRoom() {
//        return new ArrayList<>(chatRooms.values());
//    }

    /**
     * 채팅방 생성
     *
     * @param chatRoomDto 생성되는 채팅방의 정보
     */

//    public void createChatRoom(ChatRoomDto chatRoomDto) {
//        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
////                .id(chatRoomDto.getId())
//                .title(chatRoomDto.getTitle())
//                .openDate(chatRoomDto.getOpenDate())
//                .closeDate(chatRoomDto.getCloseDate())
//                .build();
//        chatRoomRepository.save(chatRoom);
//    }




    /**
     * 체팅방을 해방 채팅방의 id로 찾는다
     *
     * @param id 채팅방의 id
     * @return 해당 id에 일치하는 채팅방 객체
     */
    public ChatRoomDto findRoomById(String id) {
        return chatRooms.get(id);

    }


    /**
     * 채팅방 입장
     * <p>
     * 입장하려는 채팅방이 존재하고, 그 채팅방이 공모주에 연결되어 있고, 해당 공모주를 즐겨찾기했는지 확인한다
     *
     * @param chatDataDto 해당 채팅방의 정보
     */
    public void joinChat(ChatDataDto chatDataDto) {


        //채팅방 존재 유무 확인
        Optional<ChatRoomEntity> chatRoomEntityOptional = chatRoomRepository.findById(chatDataDto.getChatRoomId());
        ChatRoomEntity chatRoom;
        if (chatRoomEntityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다");
        } else {
            chatRoom = chatRoomEntityOptional.get();
        }

        //공모주 존재여부 확인
        Optional<StockEntity> stockEntityOptional = stockRepository.findById(chatRoom.getStockId().getId());
        StockEntity stock;
        if (stockEntityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공모주가 존재하지 않습니다");
        } else {
            stock = stockEntityOptional.get();
        }

        //발신 사용자 존재여부
        Optional<UserEntity> userEntityOptional = userRepository.findByUserName(chatDataDto.getSender());
        UserEntity user = null;
        if (userEntityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공모주가 존재하지 않습니다");
        } else {
            user = userEntityOptional.get();
        }

        //즐겨찾기 여부 확인
        if (user.getStocks().contains(stock) == false) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기가 되어있지 않은 공모주입니다");
        }
        //유저 추가 엔티티에 매핑 건의
        //기본메시지 강제 발송
        chatRoom.setUserCount(chatRoom.getUserCount() + 1);
    }

    public void sendMessage(ChatDataDto chatData){
        joinChat(chatData);
        if(ChatDataEntity.MessageType.ENTER.equals(chatData.getType())){
            //현재 시간을 가져오는 줄 필요
            chatData.setMessage(chatData.getSender() + "님이 입장하셨습니다.");
        }
        messagingTemplate.convertAndSend("/sub/{stockName}/chat" + chatData.getChatRoomId(), chatData);
    }
}


