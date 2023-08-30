package com.est.gongmoja.service;

import com.est.gongmoja.entity.NewsEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.repository.NewsRepository;
import com.est.gongmoja.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



@Service
@Slf4j
@RequiredArgsConstructor
public class NewsCrawlingService {
    private final NewsRepository newsRepository;
    private final StockRepository stockRepository;

    // 수정상항
    //@PostConstruct
    //@Scheduled(cron = "0 */10 * * * *")
    public void getCrawlNewsData() throws InterruptedException {
        List<StockEntity> stockEntityList = stockRepository.findAll(); // db에 있는 전체 주식

        int count = 1; // 크롤 한 주식개수
        for (StockEntity entity : stockEntityList) {
            if (count++ % 9 == 0) Thread.sleep(3000 + (int)(Math.random() * 1000)); // 주식 7개 크롤하면 3초 쉬는 count
            String stockName = entity.getName();
            // 대상 웹 페이지 URL, 어디까지 크롤링할 지 정해야함.
            String url = "https://search.naver.com/search.naver?where=news&sm=tab_pge&query=" + stockName + "+공모" ;
            try {
                Document document = Jsoup.connect(url).get();
                Elements articleElements = document.select("ul.list_news li");
                int cnt = 0; // 크롤한 뉴스기사 수
                for (Element articleElement : articleElements) {
                    // 각 기사의 제목, 요약 내용, 링크 가져오기
                    Element titleElement = articleElement.select("a.news_tit").first();
                    Element summaryElement = articleElement.select("div.news_dsc").first();
                    Element sourceElement = articleElement.select("a.info.press").first();

                    if (titleElement != null && summaryElement != null && sourceElement != null) {
                        Element imageElement = sourceElement.select("img.thumb").first();
                        String title = titleElement.text(); // 뉴스 제목
                        String summary = summaryElement.text(); // 뉴스 요약
                        String articleUrl = titleElement.attr("href"); // 뉴스기사 링크
                        String sourceName = sourceElement.text().replaceAll("언론사 선정", ""); // 뉴스 발행사
                        String imageUrl = imageElement.attr("data-lazysrc"); // 언론사 이미지

                        if (!imageUrl.startsWith("http"))
                            imageUrl = "https://www.google.com/s2/favicons?domain=example.com";

                        // 뉴스 발행일
                        Elements timeElements = articleElement.select("span.info");
                        Pattern pattern = Pattern.compile("\\d{4}\\.\\d{2}\\.\\d{2}");
                        Matcher matcher = pattern.matcher(timeElements.text());
                        String timeInfo = "";
                        LocalDateTime parsedDateTime = null;
                        if (matcher.find()) {
                            timeInfo = matcher.group();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
                            parsedDateTime = LocalDateTime.parse(timeInfo+".00.00.00", formatter);
                        } else {
                            char time = timeElements.text().charAt(0);
                            String temp = timeElements.text().substring(1);
                            if (temp.startsWith("시")){
                                LocalDateTime dateTime = LocalDateTime.now();
                                parsedDateTime = dateTime.minusHours(time-'0');
                            } else {
                                LocalDateTime dateTime = LocalDateTime.now();
                                parsedDateTime = dateTime.minusDays(time-'0');
                            }
                        }

                        NewsEntity newsEntity = NewsEntity.builder()
                                .newsUrl(articleUrl)
                                .content(summary)
                                .publishedTime(parsedDateTime)
                                .imageUrl(imageUrl)
                                .title(title)
                                .publisher(sourceName)
                                .stock(entity)
                                .build();

                        Optional<NewsEntity> optionalNewsEntity = newsRepository.findByNewsUrl(articleUrl);
                        if (optionalNewsEntity.isEmpty()) {
                            newsRepository.save(newsEntity);
                        }
                        if (cnt++ > 1) break; // 하나의 주식 당 3개의 뉴스 크롤
                    }
                }
            } catch (HttpStatusException e) {
                if (e.getStatusCode() == 403)
                    log.warn(stockName + " Access to the website is forbidden. Continuing with next task...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}