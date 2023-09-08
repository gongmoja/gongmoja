package com.est.gongmoja.service;

import com.est.gongmoja.entity.AnswerEntity;
import com.est.gongmoja.entity.QuestionEntity;

import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.QuestionRepository;

import com.est.gongmoja.repository.UserRepository;
import jakarta.persistence.Id;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    private final UserRepository userRepository; // user 정보 추가

    public List<QuestionEntity> getList() {
        return questionRepository.findAll();
    }

    public QuestionEntity getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Page<QuestionEntity> getList(int page) {
//        List<Sort.Order> sorts = List.of(Sort.Order.desc("createDate"));
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findAll(pageable);
    }

    public Page<QuestionEntity> getListByUser(int page, Long userId) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return questionRepository.findAllByUserId(pageable, userId);
    }


    private Specification<QuestionEntity> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<QuestionEntity> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<QuestionEntity, UserEntity> u1 = q.join("username", JoinType.LEFT);
                Join<QuestionEntity, AnswerEntity> a = q.join("answerList", JoinType.LEFT);
                Join<AnswerEntity, UserEntity> u2 = a.join("username", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public void create(String subject, String content, MultipartFile imageFile, UserEntity user) throws IOException {
        QuestionEntity question = new QuestionEntity();
        question.setSubject(subject);
        question.setContent(content);
        question.setUser(user);
        question.setCreateDate(LocalDateTime.now());
        questionRepository.save(question);

        String projectPath = System.getProperty("user.dir") + "/static";

        if (imageFile != null && !imageFile.isEmpty()) {
            // userName 및 questionId에 대한 디렉터리가 없으면 생성합니다.
            String userDirectory = projectPath + "/" + question.getId();
            File userDirectoryFile = new File(userDirectory);
            if (!userDirectoryFile.exists()) {
                userDirectoryFile.mkdirs();
            }

            String originalFileName = imageFile.getOriginalFilename();
            File saveFile = new File(userDirectory, originalFileName);

            imageFile.transferTo(saveFile);

            question.setOriginalFileName(originalFileName);
            question.setFilePath("/static/" + question.getId() + "/" + originalFileName);
            questionRepository.save(question); // 파일 정보 업데이트
        }
    }
}