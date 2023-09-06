package com.est.gongmoja.controller;

import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UpdatedNewsEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebController {
    private final UserService userService;
    private final UpdatedNewsSerivce newsService;
    private final StockService stockService;
    private final GraphService graphService;

    @GetMapping("/")
    public String mainPage(Model model, Authentication authentication){
        //비 로그인이면 바로 메인페이지 리턴
        if(authentication == null){
            return "index";
        }

        //만일 로그인 했으면 user 객체를 가져온다
        UserEntity user = (UserEntity) authentication.getPrincipal();
        UserEntity userEntity = userService.getUser(user.getUserName());
        //공모주 최신 뉴스
        List<UpdatedNewsEntity> newsList = newsService.findAll();
        //청약 진행중인 공모주
        LocalDateTime now = LocalDateTime.now();
        List<StockEntity> progressStocks = stockService.findStockByDate(now);
        //청약 예정인 공모주
        List<StockEntity> scheduledStocks = stockService.findStockByAfterDate(now);
        //model 에 userEntity,newsList 객체 넣는다
        model.addAttribute("userEntity",userEntity);
        model.addAttribute("newsList", newsList);
        model.addAttribute("progressStocks", progressStocks);
        model.addAttribute("scheduledStocks", scheduledStocks);

        //그래프 넘기기
        try {
            String base64Image = graphService.generateGraph();
            model.addAttribute("base64Image", base64Image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "index";
    }
}
