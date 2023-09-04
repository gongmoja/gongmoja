package com.est.gongmoja.repository;

import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.StockEntity;
import com.est.gongmoja.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    //유저아이디로 객체조회
    Optional<UserEntity> findByUserName(String username);
    //이메일로 객체조회
    Optional<UserEntity> findByEmail(String email);
}