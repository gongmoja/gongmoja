package com.est.gongmoja.controller;

import com.est.gongmoja.service.CalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
@Slf4j
public class CalendarController {

    @Autowired
    CalendarService calendarService;

    @RequestMapping //기본 페이지 표시
    public String viewCalendar(){
        log.info("calendar출력완료");
        return "/calendar/calendar"; // 경로 수정
    }

    @GetMapping("/event") //ajax 데이터 전송 URL
    public @ResponseBody List<Map<String, Object>> getEvent() {
        return calendarService.getEventList();
    }
}