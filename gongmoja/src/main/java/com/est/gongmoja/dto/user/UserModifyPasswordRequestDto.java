package com.est.gongmoja.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModifyPasswordRequestDto {
    private String nowPassword;
    private String newPassword;
    private String newPasswordOneMoreTime;
}
