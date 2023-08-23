package com.est.gongmoja.service;


import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {

    public List<Map<String, Object>> getEventList() {
        Map<String, Object> event = new HashMap<String, Object>();
        List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();
        event.put("start", "23-08-11");
        event.put("title", "공모주달력테스트1");
        event.put("end","23-08-12");
        eventList.add(event);
        event = new HashMap<String, Object>();
        event.put("start", LocalDate.now());
        event.put("title", "공모주2");
        event.put("end",LocalDate.now());
        eventList.add(event);
        return eventList;
    }
}

