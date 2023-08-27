package com.est.gongmoja.service;

import com.est.gongmoja.entity.QuestionEntity;

import com.est.gongmoja.repository.QuestionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<QuestionEntity> getList() {
        return questionRepository.findAll();
    }

    public QuestionEntity getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Page<QuestionEntity> getList(int page) {
        List<Sort.Order> sorts = List.of(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }

    public void create(String subject, String content, MultipartFile imageFile) throws IOException {
        QuestionEntity question = new QuestionEntity();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());

        String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files";
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + imageFile.getOriginalFilename();
        File saveFile = new File(projectPath, fileName);
        imageFile.transferTo(saveFile);
        question.setFileName(fileName);
        question.setFilePath("/files/" + fileName);

        questionRepository.save(question);
    }
}


    // 질문 등록
//    public void create(String subject, String content, UserEntity user) throws IOException {
//        QuestionEntity q = new QuestionEntity();
//        q.setSubject(subject); // 제목
//        q.setContent(content); // 내용
//        q.setCreateDate(LocalDateTime.now()); // 작성시간
//        q.setUser(user);
//        this.questionRepository.save(q);
//    }