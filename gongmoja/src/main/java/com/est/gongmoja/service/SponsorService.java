package com.est.gongmoja.service;


import com.est.gongmoja.entity.SponsorEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.repository.SponsorRepository;
import com.est.gongmoja.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SponsorService {
    private final StockRepository stockRepository;
    private final SponsorRepository sponsorRepository;

    @PostConstruct
//    @Scheduled(cron = "0 */10 * * * *")
    public void getSponsorData()  {
        List<StockEntity> stockEntityList = stockRepository.findAll(); // 모든 공모주 데이터

        for (StockEntity stock : stockEntityList){
            String sponsors = stock.getSponsor(); // 공백으로 구분된 주간사 문자열
            String[] splitedSponsors = sponsors.split(" ");
            for (String str : splitedSponsors){
                SponsorEntity entity = SponsorEntity.builder()
                        .sponsorName(str) //종목명
                        .stock(stock) //주간사
                        .build();

                sponsorRepository.save(entity);
            }
        }
    }



}
