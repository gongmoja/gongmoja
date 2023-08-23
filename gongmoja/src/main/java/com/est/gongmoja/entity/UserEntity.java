package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;
    private String password; //null 이어도 되게 구현해야할 듯. 소셜로그인 할때는 password 를 기입할 필요가 없어서
    private String email;
    private String nickName;
    private int role;

    // 소셜로그인 제공자 문자값
    private String provider;
    private String providerId;

    @OneToMany(mappedBy = "user")
    final private List<QuestionEntity> questionEntities = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "favorites")
    private List<StockEntity> stocks;
}
