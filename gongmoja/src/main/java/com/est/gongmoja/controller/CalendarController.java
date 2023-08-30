package com.est.gongmoja.controller;

import com.est.gongmoja.service.CalendarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
@RequiredArgsConstructor
@Slf4j
public class CalendarController {
    private final CalendarService calendarService;

    @GetMapping //기본 캘린더
    public String viewCalendar(){
        log.info("calendar출력완료");
        return "/calendar/calendar"; // 경로 수정
    }

    @RequestMapping(value="/event", method = RequestMethod.GET)
    @ResponseBody // json 형태이므로
    public List<Map<String, Object>> getEvent()
    {
        return calendarService.getEventList();
    }
}