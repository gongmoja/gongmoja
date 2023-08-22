package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "answers")
public class AnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 번호

    private String content; // 답변 내용
    private LocalDateTime createDate; // 날짜

    // 질문
    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity questionEntity;
}
