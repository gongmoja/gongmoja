package com.est.gongmoja.service;

import com.est.gongmoja.entity.AnswerEntity;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerEntity create(QuestionEntity questionEntity, String content, UserEntity user) {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setContent(content);
        answerEntity.setCreateDate(LocalDateTime.now());
        answerEntity.setQuestionEntity(questionEntity);
        answerEntity.setUser(user);
        this.answerRepository.save(answerEntity);
        return answerEntity;
    }

    public AnswerEntity getAnswer(Long id) {
        Optional<AnswerEntity> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();
        } else {
            throw new ResponseStatusException(ErrorCode.TOKEN_NOT_FOUND.getHttpStatus());
        }
    }
}