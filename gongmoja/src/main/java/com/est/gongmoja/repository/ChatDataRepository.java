package com.est.gongmoja.repository;

import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatDataRepository extends JpaRepository<ChatDataEntity,Long> {
    Optional<ChatDataEntity> findByUser(UserEntity user);
}
