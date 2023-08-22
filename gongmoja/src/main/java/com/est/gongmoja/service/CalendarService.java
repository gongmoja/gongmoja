package com.est.gongmoja.service;

import com.est.gongmoja.entity.CalendarEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {

    @Autowired
    private CalendarRepository calendarRepository;

    public List<Map<String, Object>> getEventList() {
        List<CalendarEntity> calendarEvents = calendarRepository.findAll();

        List<Map<String, Object>> eventList = new ArrayList<>();
        for (CalendarEntity event : calendarEvents) {
            Map<String, Object> eventData = new HashMap<>();

//            StockEntity stock = event.getStockEntity();
//            eventData.put("start", stock.getStartDate());
//            eventData.put("end", stock.getEndDate());
//            eventData.put("title", stock.getName());
//
//            eventList.add(eventData);
        }
        return eventList;
    }
}

