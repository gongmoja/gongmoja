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

import java.util.Optional;

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
}
