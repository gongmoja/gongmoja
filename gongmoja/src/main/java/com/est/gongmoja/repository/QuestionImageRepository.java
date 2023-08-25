package com.est.gongmoja.repository;

import com.est.gongmoja.entity.QuestionImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionImageRepository extends JpaRepository<QuestionImageEntity, Long> {

    // questionId
    Optional<List<QuestionImageEntity>> findAllByQuestion_Id(Long questionId);
}