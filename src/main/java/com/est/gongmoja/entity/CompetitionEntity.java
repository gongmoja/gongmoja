package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "competition_rate")
public class CompetitionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime updateTime; // 크롤링 시각
    private String stockName;
    private String totalRate; // 통합경쟁률
    private String proportionalRate; // 비례경쟁률
    private int gongmoCount; // 청약건수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sponsor_id")
    private SponsorEntity sponsor;
}