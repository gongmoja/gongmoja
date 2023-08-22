package com.est.gongmoja.dto.question;

import com.est.gongmoja.dto.user.UserDto;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.QuestionImageEntity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDto {
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private UserDto userDto;
    private List<String> imageUrls; // 첨부된 이미지 url

}