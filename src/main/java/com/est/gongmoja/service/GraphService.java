package com.est.gongmoja.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GraphService {
    private final Resource csvResource = new ClassPathResource("Book1.csv");
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    //@Scheduled(cron = "0 */10 * * * *") // 매 10분(600000밀리초)마다 실행
    public String generateGraph() {
        List<Date> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        // 오전 8시 이전에는 데이터 0으로 초기화
        Date currentTime = new Date();
        Date eightAM = new Date();
        eightAM.setHours(8);
        eightAM.setMinutes(0);
        eightAM.setSeconds(0);
        if (currentTime.before(eightAM)){
            xData.add(new Date(0));
            yData.add(0.0);
        }

        try (InputStream inputStream = csvResource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int cnt = 0;
            while ((line = br.readLine()) != null) {
                if (cnt % 30 == 0) {
                    String[] data = line.split(",");
                    String timeStr = data[0];
                    String competitionRate = data[3];

                    Date time = convertTimeToDate(timeStr);
                    if (time.getHours() < currentTime.getHours() ||
                            (time.getHours() == currentTime.getHours() && time.getMinutes() < currentTime.getMinutes())) {
                        xData.add(time);
                        yData.add(Double.parseDouble(competitionRate));
                    }
                }
                cnt++;
            }
            return generateAndSaveChart(xData, yData);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String generateAndSaveChart(List<Date> xData, List<Double> yData) {
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Daishin16Spac").xAxisTitle("Time").yAxisTitle("Competition Rate").build();
        chart.getStyler().setChartBackgroundColor(java.awt.Color.WHITE);
        chart.getStyler().setXAxisLabelRotation(0);
        chart.getStyler().setXAxisLabelAlignment(Styler.TextAlignment.Right);

        chart.addSeries("Daishin Securities", xData, yData).setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        try {
            BufferedImage chartImage = BitmapEncoder.getBufferedImage(chart);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(chartImage, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            String encodedImage = Base64.encodeBase64String(imageBytes);

            return encodedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Date convertTimeToDate(String timeStr) {
        try {
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}