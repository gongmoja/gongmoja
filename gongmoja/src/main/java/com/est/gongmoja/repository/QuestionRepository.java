package com.est.gongmoja.repository;

import com.est.gongmoja.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    QuestionEntity findBySubject(String subject);
    QuestionEntity findBySubjectAndContent(String subject, String content);
    List<QuestionEntity> findBySubjectLike(String subject);
    Page<QuestionEntity> findAll(Pageable pageable);

    // 페이징 추가
    Page<QuestionEntity> findAll(Specification<QuestionEntity> spec, Pageable pageable);
}