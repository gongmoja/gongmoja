package com.est.gongmoja;

import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.repository.AnswerRepository;
import com.est.gongmoja.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class QuestionTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Test
    @DisplayName("질문 등록 테스트 1")
    void testJpa() {
        QuestionEntity q1 = new QuestionEntity();
        q1.setTitle("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해서 알고 싶습니다.");
        q1.setCreatedAt(LocalDateTime.now());
        this.questionRepository.save(q1);  // 첫번째 질문 저장

        QuestionEntity q2 = new QuestionEntity();
        q2.setTitle("스프링부트 모델 질문입니다.");
        q2.setContent("id는 자동으로 생성되나요?");
        q2.setCreatedAt(LocalDateTime.now());
        this.questionRepository.save(q2);  // 두번째 질문 저장
    }

    @Test
    @DisplayName("질문 조회 테스트 1")
    void testJpa1() {
        List<QuestionEntity> all = this.questionRepository.findAll();
        assertEquals(2, all.size());

        QuestionEntity questionEntity = all.get(0);
        assertEquals("sbb가 무엇인가요?", questionEntity.getTitle());
    }


    @Test
    @DisplayName("질문 제목으로 조회 테스트")
    void testJpa3() {
        QuestionEntity questionEntity = this.questionRepository.findByTitle("sbb가 무엇인가요?");
        assertEquals(1, questionEntity.getId());
    }

    @Test
    @DisplayName("데이터 제목+내용으로 조회 테스트")
    void testJpa4() {
        QuestionEntity questionEntity = this.questionRepository.findByTitleAndContent(
                "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
        assertEquals(1, questionEntity.getId());
    }

    @Test
    @DisplayName("특정 문자열로 조회 테스트")
    void testJpa5() {
        List<QuestionEntity> qList = this.questionRepository.findByTitleLike("sbb%");
        QuestionEntity questionEntity = qList.get(0);
        assertEquals("sbb가 무엇인가요?", questionEntity.getTitle());
    }
}