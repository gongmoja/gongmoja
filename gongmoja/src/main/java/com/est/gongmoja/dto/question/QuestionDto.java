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
public class QuestionDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private UserDto userDto;
    private List<QuestionImageEntity> questionImageEntityList;

    public QuestionDto(Long id, String title, String content, LocalDateTime createdAt, UserDto userDto, List<QuestionImageEntity> questionImageEntityList) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.userDto = userDto;
        this.questionImageEntityList = questionImageEntityList;
    }
}
