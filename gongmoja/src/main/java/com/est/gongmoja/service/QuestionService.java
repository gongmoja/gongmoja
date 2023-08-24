package com.est.gongmoja.service;

import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.QuestionRepository;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<QuestionEntity> getList(){
        return questionRepository.findAll();
    }

    // 질문 조회
    public QuestionEntity getQuestion(Long id) {
        Optional<QuestionEntity> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new ResponseStatusException(ErrorCode.TOKEN_NOT_FOUND.getHttpStatus());
        }
    }
    // 질문 등록
    public void create(String subject, String content) {
        QuestionEntity q = new QuestionEntity();
        q.setSubject(subject); // 제목
        q.setContent(content); // 내용
        q.setCreateDate(LocalDateTime.now()); // 작성시간
        this.questionRepository.save(q);
    }
}