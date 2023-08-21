package com.est.gongmoja.repository;

import com.est.gongmoja.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<StockEntity,Long> {
    Optional<StockEntity> findByName(String stockName);
}
