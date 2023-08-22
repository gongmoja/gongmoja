package com.est.gongmoja.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RefreshTokenEntity {
    @Id
    private String userName;
    @Column(nullable = false)
    private String refreshToken;


}
