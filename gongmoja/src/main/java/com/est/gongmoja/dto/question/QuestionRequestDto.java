package com.est.gongmoja.dto.question;

import com.est.gongmoja.dto.user.UserDto;
import com.est.gongmoja.entity.QuestionImageEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class QuestionRequestDto {
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private UserDto userDto;
    private List<String> imageUrls; // 첨부된 이미지 url

    public QuestionRequestDto(Long userId, String title, String content, LocalDateTime createdAt, UserDto userDto, List<String> imageUrls) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userDto = userDto;
        this.imageUrls = imageUrls;
    }
}
