package com.est.gongmoja.repository;

import com.est.gongmoja.entity.ChatRoomEntity;
import com.est.gongmoja.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity,Long> {
    Optional<ChatRoomEntity> findByTitle(String title);
    List<ChatRoomEntity> findAll(Sort sort);

    Page<ChatRoomEntity> findByUsers(UserEntity userEntity, Pageable pageable);
}
