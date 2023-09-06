package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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

    private String filename; // 파일 이름

    private String filepath; // 파일 경로

    // userid
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 답변
    @OneToMany(mappedBy = "questionEntity", cascade = CascadeType.REMOVE) // 질문 삭제하면 답변도 전체 삭제
    private List<AnswerEntity> answerEntityList;

    public void setFileName(String fileName) {
        this.filename = fileName;
    }

    public void setFilePath(String filePath) {
        this.filepath = filePath;
    }

}