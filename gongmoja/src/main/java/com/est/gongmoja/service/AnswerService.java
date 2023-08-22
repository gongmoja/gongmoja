package com.est.gongmoja.service;

import com.est.gongmoja.entity.AnswerEntity;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    // 답변 저장
    public void create(QuestionEntity questionEntity, String content){
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setContent(content);
        answerEntity.setCreateDate(LocalDateTime.now());
        answerEntity.setQuestionEntity(questionEntity);
        this.answerRepository.save(answerEntity);
    }
}
