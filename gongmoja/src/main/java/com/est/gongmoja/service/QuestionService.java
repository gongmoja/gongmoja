package com.est.gongmoja.service;

import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.repository.QuestionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    // 질문 조회
    public QuestionEntity getQuestion(Long id) {
        Optional<QuestionEntity> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
    // 질문 등록 (이미지 저장)
    public void create(String subject, String content, MultipartFile imageUrl) {
        QuestionEntity q = new QuestionEntity();
        q.setSubject(subject); // 제목
        q.setContent(content); // 내용
        q.setCreateDate(LocalDateTime.now()); // 작성시간
        this.questionRepository.save(q);
    }
}