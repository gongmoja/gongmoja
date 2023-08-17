package com.est.gongmoja.repository;

import com.est.gongmoja.entity.ChatDataEntity;
import com.est.gongmoja.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatDataRepository extends JpaRepository<ChatDataEntity,Long> {
}
