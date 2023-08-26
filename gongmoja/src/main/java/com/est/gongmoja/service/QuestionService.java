package com.est.gongmoja.service;

import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.QuestionImageEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.QuestionImageRepository;
import com.est.gongmoja.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionImageRepository questionImageRepository;

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

    // 페이징 추가
    public Page<QuestionEntity> getList(int page) {
        // 작성일시 최신순으로 조회
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        // 게시글 10개 단위로 페이징
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return this.questionRepository.findAll(pageable);
    }

    // 질문 등록
    public void create(String subject, String content) throws IOException {
        QuestionEntity q = new QuestionEntity();
        q.setSubject(subject); // 제목
        q.setContent(content); // 내용
        q.setCreateDate(LocalDateTime.now()); // 작성시간
        this.questionRepository.save(q);
    }
}