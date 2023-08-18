package com.est.gongmoja.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequestDto {
    private String username;
    private String password;
    private String nickname;
    private String email;
}
