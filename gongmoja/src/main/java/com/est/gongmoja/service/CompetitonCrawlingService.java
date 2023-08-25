package com.est.gongmoja.service;

import com.est.gongmoja.entity.CompetitionEntity;
import com.est.gongmoja.entity.SponsorEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.repository.CompetitionRepository;
import com.est.gongmoja.repository.SponsorRepository;
import com.est.gongmoja.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompetitonCrawlingService {
    private final SponsorRepository sponsorRepository;
    private final CompetitionRepository competitionRepository;

    //    @Scheduled(cron = "0 */10 * * * *") // 10분마다 한번 업데이트 (변경완료)
    @Scheduled(cron = "0 */1 * * * *") // 매 분마다 실행
    @PostConstruct
    public void getCompetitionCrawlData() throws IOException {
        String sponor = "대신증권";
        List<SponsorEntity> sponsorEntityList
                = sponsorRepository.findAllBySponsorName(sponor);

        for (SponsorEntity sponsor: sponsorEntityList){
            log.info("Yes"+sponsor.getSponsorName());
        }



        String url = "https://www.daishin.com/g.ds?m=194&p=1031&v=681";
        String stockName = "", totalRate = "", proportionalRate = "";
        int gongmoCount;
        LocalDateTime updateTime;

        try {
            Document document = Jsoup.connect(url).get();

            // TODO 고도화시 child(4) 아닌
            //  테이블에서 청약 기간이 당일에 해당하는 데이터만 가져오는 조건문 추가 구현 해야 함
            String date = document.select("tr:nth-child(4) > td.left > div > a").text(); // 공모기간
            totalRate = document.select("tr:nth-child(4) > td:nth-child(5) > div").text(); // 총 경쟁률
            proportionalRate = document.select("tr:nth-child(4) > td:nth-child(6) > div").text(); // 비례 경쟁률
            String gongmoCnt = document.select("tr:nth-child(4) > td:nth-child(7) > div").text();
            gongmoCount = Integer.parseInt(gongmoCnt.replace(",", ""));
            updateTime = LocalDateTime.now(); // 크롤링 시각 업데이트

            // entity build
            CompetitionEntity competition = CompetitionEntity.builder()
                    .updateTime(updateTime)
                    .stockName(stockName)
                    .totalRate(totalRate)
                    .proportionalRate(proportionalRate)
                    .gongmoCount(gongmoCount)
//                    .sponsor()
                    .build();
            competitionRepository.save(competition);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}