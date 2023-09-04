package com.est.gongmoja.controller;

import com.est.gongmoja.entity.NewsEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.service.NewsService;
import com.est.gongmoja.service.StockService;
import com.est.gongmoja.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/stock")
public class StockController {
    private final StockService stockService;
    private final UserService userService;
    private final NewsService newsService;

    @GetMapping("/{stockId}")
        public String displayStock(Model model, @PathVariable("stockId")Long stockId, Authentication authentication){
            List<NewsEntity> newsList = newsService.findAllNews(stockId);
            UserEntity user = (UserEntity) authentication.getPrincipal();
            UserEntity userEntity = userService.getUser(user.getUserName());
            StockEntity stock = stockService.findStockById(stockId);
            model.addAttribute("stock", stock);
            model.addAttribute("newsList", newsList);
            model.addAttribute("userEntity", userEntity);
            return "stock/stock";
    }
}
