package com.est.gongmoja.service;

import com.est.gongmoja.entity.UpdatedNewsEntity;
import com.est.gongmoja.repository.UpdatedNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdatedNewsSerivce {
    private final UpdatedNewsRepository newsRepository;
    public List<UpdatedNewsEntity> findAll(){
        return newsRepository.findAll();
    }
}
