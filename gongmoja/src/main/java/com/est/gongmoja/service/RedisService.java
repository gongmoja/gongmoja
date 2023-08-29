package com.est.gongmoja.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RedisService {
    private final RedisTemplate<String,String> redisTemplate;

    //key : username
    public String getData(String key){
        return redisTemplate.opsForValue().get(key);
    }

    //key : username , value : refreshToken
    public void setData(String key, String value){
        //저장소 안에 이미 같은 키의 밸류가 존재한다면
        if(this.getData(key) != null){
            //키:값 튜플 삭제
            this.deleteData(key);
        }
        //키:값 튜플 저장
        redisTemplate.opsForValue().set(key,value);
        log.info("redis 값 들어감");
    }

    public void deleteData(String key){
        redisTemplate.delete(key);
    }
}
