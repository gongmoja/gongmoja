package com.est.gongmoja.service;

import com.est.gongmoja.entity.StockEntity;
import lombok.Builder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
public class CrawlingService {
    static List<StockEntity> stockEntityList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String year = "2023";
        String month = "6";
        String url = "http://www.ipostock.co.kr/sub03/ipo04.asp?str1=2023&str2="+month; // 대상 웹 페이지 URL, 어디까지 크롤링할 지 정해야함.
        try {
            Document document = Jsoup.connect(url).get();
            Elements rows2 = document.select("tr[bgcolor=#f8fafc]:contains(원)"); // 파란박스 종목
            Elements rows3 = document.select("tr[bgcolor=#ffffff]:contains(원)"); // 하얀박스 종목
            Elements comb = new Elements();
            comb.addAll(rows2);
            comb.addAll(rows3);
            for (Element row : comb) {
                Elements columns = row.select("td");
                List<String> strings = new ArrayList<>();
                for (Element column : columns) {
                    //System.out.print(column.text() + "\t");
                    strings.add(column.text());
                }
                strings.remove(0); // 비어있는 추천 란 삭제
                System.out.println(strings.toString());

                // 날짜정보 parsing
                if (strings.get(0).equals("공모철회")){
                    continue;
                } else {
                    String[] dates = strings.get(0).split("~");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
                    LocalDateTime startDate = LocalDateTime.parse(year + "." + dates[0].substring(0, dates[0].length() - 1) + ".00.00.00", formatter);
                    formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss");
                    LocalDateTime endDate = LocalDateTime.parse(year + "." + dates[1].substring(1) + ".00.00.00", formatter);
                    Element linkElement = row.select("a[href]").first();
                    if (linkElement != null) {
                        String link = linkElement.attr("href");
                        String newUrl = "http://www.ipostock.co.kr" + link; // ipostock사이트에서 종목명 클릭하면 나오는 url
                        System.out.println(newUrl);
                        Document specificDocument = Jsoup.connect(newUrl).get();
                        // 공모주 풀네임
                        Element strongTitleElement = specificDocument.select("strong.view_tit").first();
                        String title = strongTitleElement.text();
                        System.out.println(title);
                        // 종목
                        Element industryElement = specificDocument.select("td:has(font[color=213894])").last();
                        String industry = industryElement.ownText().substring(1);
                        System.out.println(industry);
                        // 주식 발행량
                        Elements shareAmountElements = specificDocument.select("table.view_tb");
                        Element shareAmountElement = shareAmountElements.select("td[bgcolor=#FFFFFF]:contains(주)").first();
                        String shareAmountString = shareAmountElement.text().replaceAll("[^0-9]", "");
                        int shareAmount = Integer.parseInt(shareAmountString);
                        System.out.println(shareAmount);
                    }
                    // 공모가
                    String priceString = strings.get(3);
                    String numericPart = priceString.replaceAll("[^0-9]", "");
                    int price = Integer.parseInt(numericPart);
                    // 경쟁률
                    String competitionRate = strings.get(7).substring(1);
                    // 주간사
                    String sponsor = strings.get(8).substring(1);
//                StockEntity stockEntity = StockEntity.builder()
//                        .startDate()
//                        .endDate()
//                        .ipoDate()
//                        .refundDate()
//                        .competitionRate()
//                        .industry()
//                        .sponsor()
//                        .companyUrl()
//                        .shareAmount()
//                        .price()
//                        .minOrder()
//                        .build();
                    System.out.println("-----------------------------------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
