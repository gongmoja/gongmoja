package com.est.gongmoja.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GraphService {
    private final String csvFile = "src/main/resources/Book1.csv";
    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    //@Scheduled(cron = "0 */10 * * * *") // 매 10분(600000밀리초)마다 실행
    public String generateGraph() {
        List<Date> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        Date currentTime = new Date();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
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
        XYChart chart = new XYChartBuilder().width(800).height(600).title("대신제16호스팩").xAxisTitle("Time").yAxisTitle("비례 경쟁률").build();
        chart.getStyler().setChartBackgroundColor(java.awt.Color.WHITE);
        chart.getStyler().setXAxisLabelRotation(0);
        chart.getStyler().setXAxisLabelAlignment(Styler.TextAlignment.Right);

        chart.addSeries("대신증권", xData, yData).setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

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