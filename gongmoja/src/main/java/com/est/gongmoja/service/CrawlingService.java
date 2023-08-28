package com.est.gongmoja.service;

import com.est.gongmoja.entity.SponsorEntity;
import com.est.gongmoja.entity.StockEntity;
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
public class CrawlingService {
    private final StockRepository stockRepository;

    //@PostConstruct
    //@Scheduled(cron = "0 * * * * *") // 1분마다 한번 업데이트 (정상작동 테스트용)
//    @Scheduled(cron = "0 */10 * * * *") // 10분마다 한번 업데이트
    public void monthlyCrawl() throws IOException {
        // 크롤링 데이터 범위 [2023.6월~ 현재 month+1월]
        int currentMonth = LocalDate.now().getMonthValue(); // 현재 월(month)
        for (int i = 6; i <= currentMonth + 1; i++) {
            getCrawlData(2023, i);
        }
    }

    public void getCrawlData(int year, int month) throws IOException {

        int shareAmount = 0;
        boolean isCanceled; // 공모철회
        String stockName = "", industry = "", sponsor = "";
        LocalDateTime refundDate = null, ipoDate = null;
        String url = "http://www.ipostock.co.kr/sub03/ipo04.asp?str1=" + year + "&str2=" + month;

        try {

            // 현재 업데이트 시간 기록

            Document document = Jsoup.connect(url).get();
            Elements oddData = document.select("tr[bgcolor=#f8fafc]:contains(원)"); // 홀수 라인 종목
            Elements evenData = document.select("tr[bgcolor=#ffffff]:contains(원)"); // 짝수 라인 종목
            Elements comb = new Elements();
            comb.addAll(oddData);
            comb.addAll(evenData);

            for (Element element : comb) { // element : 한 종목 칼럼 데이터
                Elements columns = element.select("td");
                List<String> strings = new ArrayList<>();
                for (Element column : columns) {
                    strings.add(column.text());
                }
                strings.remove(0); // [0] 공백 데이터 삭제

                // 청약일정 parsing (ex. "08.29 ~ 08.30" -> 2023-08-10T10:00, 2023-08-11T16:00)
                // 공모 철회 flag
                if (strings.get(0).equals("공모철회"))
                    isCanceled = true;
                else isCanceled = false;

                // 종목별 세부링크 내에서 크롤링
                Element linkElement = element.select("a[href]").first();
                if (linkElement != null) {
                    String link = linkElement.attr("href"); // 종목 고유링크
                    String detailUrl = "http://www.ipostock.co.kr" + link;
                    Document detailDoc = Jsoup.connect(detailUrl).get();

                    // 공모주 이름
                    Element nameElem = detailDoc.select("strong.view_tit").first();
                    stockName = nameElem.text();

                    // 철회 데이터 크롤링 시 오류 발생 가능하므로 조건문으로 체크
                    if (!isCanceled) { // 철회 아닐 경우
                        // 분야
                        Element industryElem = detailDoc.select("td:has(font[color=213894])").last();
                        industry = industryElem.ownText().substring(1);

                        // 주식 총발행량
                        Elements shareAmountElem = detailDoc.select("table.view_tb");
                        Element detailShareAmountElem = shareAmountElem.select("table.view_tb > tbody > tr:nth-child(4) > td:nth-child(2) > b").first();
                        String shareAmountStr = detailShareAmountElem.text().replaceAll("[^0-9]", "");
                        shareAmount = Integer.parseInt(shareAmountStr);

                        // 주간사
                        Elements sponsorElems = detailDoc.select("table.view_tb");
                        String sponsorList = sponsorElems.select("td > strong:contains(증권), td > strong:contains(투자)").text();
                        sponsor = sponsorList.replace("(", "");
                    }
                }

                if (isCanceled) { // 공모철회
                    Optional<StockEntity> canceledOptionalStock = stockRepository.findByName(stockName);
                    if (canceledOptionalStock.isPresent()) {  // 이미 db에 존재시
                        StockEntity canceledStock = canceledOptionalStock.get();
                        stockRepository.delete(canceledStock);
                    }

                    // 존재 안 할 시 크롤링 stop 후 continue
                    continue;
                }


                /* 이후 크롤링 데이터들은 철회 아닌 공모주에 대해서만 진행하게 됨 */

                String[] dates = strings.get(0).split("~");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
                LocalDateTime startDate = LocalDateTime.parse(year + "." + dates[0].substring(0, dates[0].length() - 1) + ".10.00.00", formatter);
                LocalDateTime endDate = LocalDateTime.parse(year + "." + dates[1].substring(1) + ".16.00.00", formatter);

                // 환불일
                if (!strings.get(5).equals("")) {
                    refundDate = LocalDateTime.parse(year + "." + strings.get(5).substring(0, dates[0].length() - 1) + ".09.00.00", formatter);
                }

                // 상장일
                if (!strings.get(6).equals("")) {
                    ipoDate = LocalDateTime.parse(year + "." + strings.get(6).substring(0, dates[0].length() - 1) + ".09.00.00", formatter);
                }

                // 공모가
                String priceStr = strings.get(3);
                String priceStrParse = priceStr.replaceAll("[^0-9]", "");
                int price = Integer.parseInt(priceStrParse);

                // 경쟁률
                String competitionRate = strings.get(7);

                // 크롤링 시각 업데이트
                LocalDateTime updateTime = LocalDateTime.now();


                // entity build
                StockEntity stock = StockEntity.builder()
                        .startDate(startDate)
                        .endDate(endDate)
                        .name(stockName)
                        .industry(industry)
                        .shareAmount(shareAmount)
                        .price(price)
                        .competitionRate(competitionRate) // 실시간 경쟁률 아님 (기관 경쟁률)
                        .sponsor(sponsor)
                        .ipoDate(ipoDate)
                        .refundDate(refundDate)
                        .updateTime(updateTime)
                        .build();


                Optional<StockEntity> optionalStock = stockRepository.findByName(stockName);
                if (optionalStock.isEmpty()){
                    stockRepository.save(stock);}
                else { // 기존 공모주 정보 업데이트 (id, name은 업데이트 안됨)
                    StockEntity updateStock = optionalStock.get();
                    updateStock.setStartDate(startDate);
                    updateStock.setEndDate(endDate);
                    updateStock.setIndustry(industry);
                    updateStock.setShareAmount(shareAmount);
                    updateStock.setPrice(price);
                    updateStock.setCompetitionRate(competitionRate);
                    updateStock.setSponsor(sponsor);
                    updateStock.setIpoDate(ipoDate);
                    updateStock.setRefundDate(refundDate);
                    updateStock.setUpdateTime(updateTime);
                    stockRepository.save(updateStock);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}