package com.est.gongmoja.service;


import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final StockRepository stockRepository;

    public List<Map<String, Object>> getEventList() {
        List <Map<String,Object>> eventDataList = new ArrayList<Map<String,Object>> ();
        Map <String, Object> eventData = new HashMap<String, Object>();

        List<StockEntity> stockEntityList = stockRepository.findAll();
        for (StockEntity stock : stockEntityList){
            // 1.청약일 데이터
            eventData = new HashMap<String, Object>();
            eventData.put("title", "[청약] "+stock.getName());
            eventData.put("start", stock.getStartDate());
            eventData.put("end", stock.getEndDate());
            eventData.put("status", stock.getId()); //extendedProps
            eventData.put("type", "청약"); //extendedProps
            eventData.put("backgroundColor", "#FFE5F1");
            eventData.put("textColor","#000000");
            eventData.put("borderColor","rgba(0, 185, 186, 0)");
            eventData.put("url","/stock/"+stock.getId());
            eventDataList.add(eventData);

            // 2.환불일 데이터
            eventData = new HashMap<String, Object>();
            eventData.put("title", "[환불] "+stock.getName());
            eventData.put("start", stock.getRefundDate());
            eventData.put("end", stock.getRefundDate());
            eventData.put("status", stock.getId()); //extendedProps
            eventData.put("type", "환불"); //extendedProps
            eventData.put("textColor","#FBF0B2");
            eventData.put("backgroundColor", "#CAEDFF");
            eventData.put("url","/stock/"+stock.getId());
            eventDataList.add(eventData);

            // 3.상장일 데이터
            eventData = new HashMap<String, Object>();
            eventData.put("title", "[상장] "+stock.getName());
            eventData.put("start", stock.getIpoDate());
            eventData.put("end", stock.getIpoDate());
            eventData.put("status", stock.getId()); //extendedProps
            eventData.put("type", "상장"); //extendedProps
            eventData.put("textColor","#FBF0B2");
            eventData.put("backgroundColor", "#D8B4F8");
            eventData.put("url","/stock/"+stock.getId());
            eventDataList.add(eventData);
        }
        return eventDataList;
    }
}

