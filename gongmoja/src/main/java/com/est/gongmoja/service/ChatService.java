package com.est.gongmoja.service;

import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.ChatDataRepository;
import com.est.gongmoja.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatDataRepository chatDataRepository;

    /**
     *채팅방 생성
     *
     * @param id 채팅방의 id
     * @param chatRoomDto 생성되는 채팅방의 정보
     */

    public void createChatRoom(Long id, ChatRoomDto chatRoomDto){
        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .id(chatRoomDto.getId())
                .title(chatRoomDto.getTitle())
                .openDate(chatRoomDto.getOpenDate())
                .closeDate(chatRoomDto.getCloseDate())
                .build();
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 채팅방 입장
     *
     * 입장하려는 채팅방이 존재하고, 그 채팅방이 공모주에 연결되어 있고, 해당 공모주를 즐겨찾기했는지 확인한다
     *
     * @param user 채팅방에 입장하려는 사용자
     * @param chatRoomDto 해당 채팅방의 정보
     */
    public void joinChat(UserEntity user, ChatRoomDto chatRoomDto) {

        //채팅방 존재 유무 확인
        Optional<ChatRoomEntity> chatRoomEntityOptional = chatRoomRepository.findById(chatRoomDto.getId());
        ChatRoomEntity chatRoom = null;
        if (chatRoomEntityOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "채팅방이 존재하지 않습니다");
        } else {
            chatRoom = chatRoomEntityOptional.get();
        }

        //공모주 존재여부 확인
        //Optional<StockEntity> stockEntityOptional = stockRepository.findById(chatRoomDto.getStockId());
//        StockEntity stock = null;
//        if(stockEntityOptional.isEmpty()){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "공모주가 존재하지 않습니다");
//        }else{
//            stock = stockEntityOptional.get();
//        }

        //즐겨찾기 여부 확인
//        if(user.getStocks().contains(stock) == false){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "즐겨찾기가 되어있지 않은 공모주입니다");
//        }
        //웹소켓 세션을 생성해서 추가해야한다
        chatRoom.setUserCount(chatRoom.getUserCount() + 1);
        //채팅에 참여중인 인원에 추가하고 세션이 만료될경우 삭제하는 기능 추가
    }





}
