package com.est.gongmoja.repository;

import com.est.gongmoja.entity.SponsorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SponsorRepository extends JpaRepository<SponsorEntity, Long> {
    List<SponsorEntity> findAllBySponsorName(String sponsorName);
}
