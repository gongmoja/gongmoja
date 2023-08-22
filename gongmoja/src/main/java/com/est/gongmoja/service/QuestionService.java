package com.est.gongmoja.service;

import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.exception.DataNotFoundException;
import com.est.gongmoja.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Timer;


@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<QuestionEntity> getList() {
        return this.questionRepository.findAll();
    }

    // 질문 등록
    @Transactional
    public void create(String title, String content) {
        QuestionEntity questionEntity = new QuestionEntity();

        questionEntity.setTitle(title);
        questionEntity.setContent(content);
        questionEntity.setCreatedAt(LocalDateTime.now());
        this.questionRepository.save(questionEntity);
    }

    public QuestionEntity getQuestion(Long id) {
        Optional<QuestionEntity> questionEntity = this.questionRepository.findById(id);
        if (questionEntity.isPresent()) {
            return questionEntity.get();
        } else {
            throw new DataNotFoundException("질문을 찾을 수 없습니다.");
        }
    }
}