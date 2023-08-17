package com.est.gongmoja.dto;

import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;

    private String title;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private Long userCount;
    private StockEntity stockId;
    private List<ChatDataEntity> chatDataEntityList;
    private List<UserEntity> chatUserList;
}
