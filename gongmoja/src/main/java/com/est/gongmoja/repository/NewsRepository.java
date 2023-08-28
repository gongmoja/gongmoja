package com.est.gongmoja.repository;

import com.est.gongmoja.entity.NewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<NewsEntity, Long> {
    Optional<NewsEntity> findByNewsUrl(String newsUrl);

    Optional<List<NewsEntity>> findAllByStockId(Long id);
}
