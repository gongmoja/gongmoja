package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "chat_room")
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
