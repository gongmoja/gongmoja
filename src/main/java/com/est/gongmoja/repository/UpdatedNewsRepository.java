package com.est.gongmoja.repository;

import com.est.gongmoja.entity.NewsEntity;
import com.est.gongmoja.entity.UpdatedNewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpdatedNewsRepository extends JpaRepository<UpdatedNewsEntity, Long> {
    Optional<UpdatedNewsEntity> findByNewsUrl(String newsUrl);
}
