package com.est.gongmoja;

import com.est.gongmoja.entity.AnswerEntity;
import com.est.gongmoja.entity.QuestionEntity;
import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.AnswerRepository;
import com.est.gongmoja.repository.QuestionRepository;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        q1.setSubject("이번주에 하는 공모주가 어떤게 있나요?");
        q1.setContent("공모주에 대해 알고 싶습니다.");
        q1.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q1);  // 첫번째 질문 저장

        QuestionEntity q2 = new QuestionEntity();
        q2.setSubject("asdf 닉네임을 가진 사람을 신고하고싶습니다.");
        q2.setContent("신고해주세요");
        q2.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q2);  // 두번째 질문 저장
    }

    @Test
    @DisplayName("질문 조회 테스트 1")
    void testJpa1() {
        List<QuestionEntity> all = this.questionRepository.findAll();
        assertEquals(2, all.size());

        QuestionEntity questionEntity = all.get(0);
        assertEquals("sbb가 무엇인가요?", questionEntity.getSubject());
    }


    @Test
    @DisplayName("질문 제목으로 조회 테스트")
    void testJpa3() {
        QuestionEntity questionEntity = this.questionRepository.findBySubject("sbb가 무엇인가요?");
        assertEquals(1, questionEntity.getId());
    }

    @Test
    @DisplayName("데이터 제목+내용으로 조회 테스트")
    void testJpa4() {
        QuestionEntity questionEntity = this.questionRepository.findBySubjectAndContent(
                "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
        assertEquals(1, questionEntity.getId());
    }

    @Test
    @DisplayName("특정 문자열로 조회 테스트")
    void testJpa5() {
        List<QuestionEntity> qList = this.questionRepository.findBySubjectLike("sbb%");
        QuestionEntity questionEntity = qList.get(0);
        assertEquals("sbb가 무엇인가요?", questionEntity.getSubject());
    }

    @Test
    @DisplayName("댓글 등록 테스트")
    void testJpa6() {
        Long questionIdToFind = 1L;
        Optional<QuestionEntity> oq = this.questionRepository.findById(questionIdToFind);
        assertTrue(oq.isPresent());
        QuestionEntity q = oq.get();

        AnswerEntity a = new AnswerEntity();
        a.setContent("찾아보세요");
        a.setQuestionEntity(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
        a.setCreateDate(LocalDateTime.now());
        this.answerRepository.save(a);
    }

}