package com.est.gongmoja.controller;

import com.est.gongmoja.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @RequestMapping
    public String viewCalendar() {
        return "calendarPage";
    }

    @GetMapping("/event")
    public @ResponseBody List<Map<String, Object>> getEvent() {
        return calendarService.getEventList();
    }
}