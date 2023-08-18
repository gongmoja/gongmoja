package com.est.gongmoja.service;

import com.est.gongmoja.entity.NewsEntity;
import com.est.gongmoja.repository.NewsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsCrawlingService {
    private final NewsRepository newsRepository;

    @PostConstruct
    public void getCrawlNewsData() throws IOException {
        String stockName = "대신밸런스제15호스팩";
        // 대상 웹 페이지 URL, 어디까지 크롤링할 지 정해야함.
        String url = "https://search.naver.com/search.naver?where=news&sm=tab_pge&query=" + stockName + "&sort=0&photo=0&field=0&pd=0&ds=&de=&cluster_rank=27&mynews=0&office_type=0&office_section_code=0&news_office_checked=&nso=so:r,p:all,a:all&start=1";

        try {
            Document document = Jsoup.connect(url).get();
            Elements articleElements = document.select("ul.list_news li");
            int cnt = 1;
            for (Element articleElement : articleElements) {
                // 각 기사의 제목, 요약 내용, 링크 가져오기
                //if (cnt++ > 3) break;
                Element titleElement = articleElement.select("a.news_tit").first(); // 뉴스 제목
                Element summaryElement = articleElement.select("div.news_dsc").first(); // 뉴스 요약
                Element timeElement = articleElement.select("span.info").first(); // 뉴스 시간
                Element sourceElement = articleElement.select("a.info.press").first(); // 뉴스 발행사

                if (titleElement != null && summaryElement != null && timeElement != null && sourceElement != null) {
                    Element imageElement = sourceElement.select("img.thumb").first();
                    String title = titleElement.text();
                    String summary = summaryElement.text();
                    String articleUrl = titleElement.attr("href");
                    String timeInfo = timeElement.text().substring(0,timeElement.text().length() - 1);
                    String sourceName = sourceElement.text();
                    String imageUrl = imageElement.attr("data-lazysrc");

                    if (!imageUrl.startsWith("http"))
                        imageUrl = "https://www.google.com/s2/favicons?domain=example.com";

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
                    LocalDateTime publishedTime = LocalDateTime.parse(timeInfo + ".00.00.00", formatter);

                    System.out.println("Title: " + title);
                    System.out.println("Summary: " + summary);
                    System.out.println("URL: " + articleUrl);
                    System.out.println("Time Info: " + timeInfo);
                    System.out.println("Source Name: " + sourceName);
                    System.out.println("Image URL: " + imageUrl);
                    System.out.println();
                    //System.out.println(document);
                    NewsEntity newsEntity = NewsEntity.builder()
                            .newsUrl(url)
                            .content(summary)
                            .publishedTime(publishedTime)
                            .imageUrl(imageUrl)
                            .title(title)
                            .publisher(sourceName)
                            .build();
                    newsRepository.save(newsEntity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
