package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "chat_room")
public class ChatRoomEntity {

    @Id
    private String id;

    private String title;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private Long userCount;


    @OneToOne
    @JoinColumn(name = "stock_id", referencedColumnName = "id")
    private StockEntity stockId;


    @OneToMany(mappedBy = "chatRoom")
    final private List<ChatDataEntity> chatDataEntityList = new ArrayList<>();
}
