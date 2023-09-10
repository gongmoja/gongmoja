package com.est.gongmoja.service;

import com.est.gongmoja.entity.NewsEntity;
import com.est.gongmoja.entity.UpdatedNewsEntity;
import com.est.gongmoja.repository.UpdatedNewsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdatedNewsCrawlingService {
    private final UpdatedNewsRepository newsRepository;

    //@PostConstruct
    //@Scheduled(cron = "0 */10 * * * *")
    public void getUpdatedNews(){
        String url = "https://search.naver.com/search.naver?where=news&sm=tab_jum&query=%EA%B3%B5%EB%AA%A8%EC%A3%BC";
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

                    UpdatedNewsEntity newsEntity = UpdatedNewsEntity.builder()
                            .newsUrl(articleUrl)
                            .content(summary)
                            .publishedTime(parsedDateTime)
                            .imageUrl(imageUrl)
                            .title(title)
                            .publisher(sourceName)
                            .build();

                    Optional<UpdatedNewsEntity> optionalNewsEntity = newsRepository.findByNewsUrl(articleUrl);
                    if (optionalNewsEntity.isEmpty()) {
                        newsRepository.save(newsEntity);
                    }
                    if (cnt++ > 1) break; // 하나의 주식 당 3개의 뉴스 크롤
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
