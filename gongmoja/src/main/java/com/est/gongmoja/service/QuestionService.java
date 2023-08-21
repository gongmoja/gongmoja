package com.est.gongmoja.service;

import com.est.gongmoja.dto.question.QuestionRequestDto;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.QuestionImageEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.repository.QuestionImageRepository;
import com.est.gongmoja.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<QuestionEntity> getList(){
        return this.questionRepository.findAll();
    }

    public QuestionEntity getQuestion(Long id) {
        Optional<QuestionEntity> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 에러 코드 수정
        }
    }



}