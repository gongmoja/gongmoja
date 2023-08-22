package com.est.gongmoja.service;

import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.ChatDataRepository;
import com.est.gongmoja.repository.ChatRoomRepository;
import com.est.gongmoja.repository.StockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final StockRepository stockRepository;

    //채팅방 별 연결된 웹소캣 세션의 집합
    private Map<Long, Set<WebSocketSession>> chatRoomSessions = new HashMap<>();
    private Set<WebSocketSession> sessions = new HashSet<>();

    private final ObjectMapper objectMapper;

    public void addSessionToChatRoom(Long chatRoomId, WebSocketSession session) {
        chatRoomSessions.computeIfAbsent(chatRoomId, key -> new HashSet<>()).add(session);
    }

    public void handlerActions(WebSocketSession session, ChatRoomEntity chatRoom, ChatDataEntity chatData) {
        if (chatData.getType().equals(ChatDataEntity.MessageType.ENTER)) {
            chatData.setMessage(chatData.getUser().getNickName() + " 님이 입장하였습니다.");
            convertMessage(chatData);
        } else if (chatData.getType().equals(ChatDataEntity.MessageType.TALK)) {
            convertMessage(chatData);
        }
    }


    public <T> void convertMessage(T message) {
        sessions.parallelStream().forEach(session -> sendMessageToAll(session, message));
    }

    public <T> void sendMessageToAll(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            ;
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
    public ChatRoomEntity createRoom(ChatRoomDto chatRoomDto) {
        String randomId = UUID.randomUUID().toString();
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .id(123L)
                .title(chatRoomDto.getTitle())
                .openDate(chatRoomDto.getOpenDate())
                .closeDate(chatRoomDto.getCloseDate())
                .build();
        return chatRoom;
    }



    /**
     * 체팅방을 해방 채팅방의 id로 찾는다
     *
     * @param id 채팅방의 id
     * @return 해당 id에 일치하는 채팅방 객체
     */
    public ChatRoomEntity findRoomById(Long id) {
        Optional<ChatRoomEntity> chatRoomEntityOptional = chatRoomRepository.findById(id);
        //잘못된 채팅방일경우 추가해야함
        return chatRoomEntityOptional.get();

    }


    /**
     * 채팅방 입장
     * <p>
     * 입장하려는 채팅방이 존재하고, 그 채팅방이 공모주에 연결되어 있고, 해당 공모주를 즐겨찾기했는지 확인한다
     *
     * @param user        채팅방에 입장하려는 사용자
     * @param chatRoomDto 해당 채팅방의 정보
     */
//    public void joinChat(UserEntity user, ChatRoomDto chatRoomDto, WebSocketSession session) {
//
//        //채팅방 존재 유무 확인
//        Optional<ChatRoomEntity> chatRoomEntityOptional = chatRoomRepository.findById(chatRoomDto.getId());
//        ChatRoomEntity chatRoom = null;
//        if (chatRoomEntityOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다");
//        } else {
//            chatRoom = chatRoomEntityOptional.get();
//        }
//
//        //공모주 존재여부 확인
//        Optional<StockEntity> stockEntityOptional = stockRepository.findById(chatRoomDto.getStockId().getId());
//        StockEntity stock = null;
//        if (stockEntityOptional.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공모주가 존재하지 않습니다");
//        } else {
//            stock = stockEntityOptional.get();
//        }
//
//        //즐겨찾기 여부 확인
//        if (user.getStocks().contains(stock) == false) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기가 되어있지 않은 공모주입니다");
//        }
//
//        //웹소켓 세션을 생성해서 추가해야한다
//
//
//        userEnteredChatRoom(chatRoom.getId(), session);
//        chatRoom.setUserCount(chatRoom.getUserCount() + 1);
//        //채팅에 참여중인 인원에 추가하고 세션이 만료될경우 삭제하는 기능 추가
//    }


    /**
     * 채팅방의 세션그룹에 입장한 유저의 세션을 분류하는
     *
     * @param chatRoomId 유저가 입장하는 채팅방의 id
     * @param session    joinChat에서 유저에게 할당된 세션
     */
    public void userEnteredChatRoom(Long chatRoomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = chatRoomSessions.getOrDefault(chatRoomId, new HashSet<>());
        if (sessions == null) {
            sessions = new HashSet<>();
            sessions.add(session);
            chatRoomSessions.put(chatRoomId, sessions);
        } else {
            sessions.add(session);
        }
    }
}


