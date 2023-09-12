package com.est.gongmoja.service;

import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.StockRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public String findStockNameById(Long stockId){
        Optional<StockEntity> byId = stockRepository.findById(stockId);
        if(byId.isEmpty()) throw new CustomException(ErrorCode.STOCK_NOT_FOUND);
        return byId.get().getName();
    }

    public StockEntity findStockById(Long stockId){
        Optional<StockEntity> byId = stockRepository.findById(stockId);
        if(byId.isEmpty()) throw new CustomException(ErrorCode.STOCK_NOT_FOUND);
        return byId.get();
    }


    public StockEntity getStockById(Long stockId){
        return stockRepository.findById(stockId).orElseThrow(()->new CustomException(ErrorCode.USERNAME_NOT_FOUND));
    }

    public List<StockEntity> findStockByDate(LocalDateTime now){
        List<StockEntity> allStocks = stockRepository.findAll();
        List<StockEntity> progressStocks = new ArrayList<>();
        for (StockEntity stock : allStocks){
            LocalDateTime startDate = stock.getStartDate();
            LocalDateTime endDate = stock.getEndDate();
            // 현재 날짜와 시작일, 종료일을 비교하여 현재 날짜가 사이에 있는 경우 추가
            if (now.isAfter(startDate) && now.isBefore(endDate)) {
                progressStocks.add(stock);
            }
        }
        return progressStocks;
    }

    public List<StockEntity> findStockByAfterDate(LocalDateTime now){
        List<StockEntity> allStocks = stockRepository.findAll();
        List<StockEntity> scheduledStocks = new ArrayList<>();
        Collections.sort(allStocks, Comparator.comparing(StockEntity::getStartDate));
        for (StockEntity stock : allStocks){
            LocalDateTime startDate = stock.getStartDate();
            LocalDateTime endDate = stock.getEndDate();
            // 현재 날짜와 시작일, 종료일을 비교하여 현재 날짜가 사이에 있는 경우 추가
            if (startDate.isAfter(now)) {
                scheduledStocks.add(stock);
                if (scheduledStocks.size() == 3) break;
            }
        }
        return scheduledStocks;
    }
}
