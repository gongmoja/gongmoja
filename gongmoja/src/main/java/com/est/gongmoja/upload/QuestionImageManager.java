package com.est.gongmoja.upload;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class QuestionImageManager {
    public String upload(MultipartFile multipartFile, Long questionId, Long userId) {
        String Questions = String.format("media/%s/%s/", "user_" + userId, "question_" + questionId);
        return "";
    }
}
