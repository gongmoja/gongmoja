package com.est.gongmoja.service;

import com.est.gongmoja.entity.NewsEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public List<NewsEntity> findAllNews(Long stockId){
        Optional<List<NewsEntity>> allByStockId = newsRepository.findAllByStockId(stockId);
        if (allByStockId.isEmpty()) throw new CustomException(ErrorCode.NEWS_NOT_FOUND);
        return allByStockId.get();
    }

}
