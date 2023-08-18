package com.est.gongmoja.repository;

import com.est.gongmoja.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionBoardRepository extends JpaRepository<QuestionEntity, Long> {
}
