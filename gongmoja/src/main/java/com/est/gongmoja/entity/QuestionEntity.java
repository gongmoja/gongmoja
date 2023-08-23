package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "questions")
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //번호

    @Column(length = 200)
    private String subject; // 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 내용

    private LocalDateTime createDate; // 작성 일시

    // userid
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 이미지
    @OneToMany(mappedBy = "question")
    private List<QuestionImageEntity> questionImageList = new ArrayList<>();

    // 답변
    @OneToMany(mappedBy = "questionEntity", cascade = CascadeType.REMOVE) // 질문 삭제하면 답변도 전체 삭제
    private List<AnswerEntity> answerEntityList;
}