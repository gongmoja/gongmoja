package com.est.gongmoja.repository;

import com.est.gongmoja.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    QuestionEntity findByTitle(String title);

    QuestionEntity findByTitleAndContent(String title, String content);

    List<QuestionEntity> findByTitleLike(String title);
}