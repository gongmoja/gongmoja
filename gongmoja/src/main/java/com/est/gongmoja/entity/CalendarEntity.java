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
@Table(name = "calendar")
public class CalendarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime stockStartDate;
    private LocalDateTime stockEndDate;

    @OneToMany(mappedBy = "calendar")
    final private List<StockEntity> stockEntityList = new ArrayList<>();
}
