package com.est.gongmoja.controller;

import com.est.gongmoja.entity.NewsEntity;
import com.est.gongmoja.service.NewsService;
import com.est.gongmoja.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;
    private final StockService stockService;

    @GetMapping("/news/{stockId}")
    public String displayNews(Model model, @PathVariable("stockId")Long stockId){
        List<NewsEntity> newsList = newsService.findAllNews(stockId);
        String stockName = stockService.findStockNameById(stockId);
        model.addAttribute("newsList", newsList);
        model.addAttribute("stockName", stockName);
        return "news";
    }
}
