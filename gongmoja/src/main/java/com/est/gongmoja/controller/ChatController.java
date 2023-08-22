package com.est.gongmoja.controller;

import com.est.gongmoja.dto.chat.ChatRoomDto;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ChatRoomEntity createRoom(@RequestBody ChatRoomDto chatRoomDto) {
        return chatService.createRoom(chatRoomDto);

    }

//    @GetMapping
//    public List<ChatRoomEntity> findAllRoom() {
//        return chatService.findAllRoom();
//    }
}
