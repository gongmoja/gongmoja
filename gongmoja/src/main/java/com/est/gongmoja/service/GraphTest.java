package com.est.gongmoja.service;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class GraphTest {
    public static void main(String[] args) {
        String csvFile = "src/main/resources/Book1.csv";
        String line;
        List<Date> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        int cnt = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // 30분에 한번 씩 그래프 표현
                if (cnt % 30 == 0) {
                    String[] data = line.split(",");
                    // 'data' 배열에 CSV 파일의 각 열 데이터가 들어감
                    String timeStr = data[0];
                    String competitionRate = data[3];

                    // 시간 데이터를 Date 객체로 변환
                    Date time = convertTimeToDate(timeStr);
                    xData.add(time); // 시간
                    yData.add(Double.parseDouble(competitionRate)); // 비례 경쟁률
                }
                cnt++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 선 그래프 스타일
        XYChart chart = new XYChartBuilder().width(800).height(600).title("대신제16호스팩").xAxisTitle("Time").yAxisTitle("비례 경쟁률").build();
        chart.getStyler().setXAxisLabelRotation(0);
        chart.getStyler().setXAxisLabelAlignment(Styler.TextAlignment.Right);

        chart.addSeries("대신증권", xData, yData).setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        new SwingWrapper<>(chart).displayChart();
    }

    private static Date convertTimeToDate(String timeStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
