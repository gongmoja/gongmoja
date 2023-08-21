package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stocks")
public class StockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime updateTime; // 마지막 크롤링 시간

    private LocalDateTime startDate; // 청약시작일
    private LocalDateTime endDate; // 청약종료일
    private LocalDateTime ipoDate; // 상장일
    private LocalDateTime refundDate; // 환불일

    private String name; // 공모주 이름
    private String competitionRate; // 경쟁률
    private String industry; // 종목
    private String sponsor; // 주간사
    private int shareAmount; // 총 발행량
    private int price; // 공모가

    @OneToMany(mappedBy = "stock")
    final private List<NewsEntity> newsEntityList = new ArrayList<>();

    @ManyToMany(mappedBy = "stocks")
    private List<UserEntity> users;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id", referencedColumnName = "id")
    private ChatRoomEntity chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar_id")
    private CalendarEntity calendar;

}
