package com.est.gongmoja.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND,"유저 정보가 존재하지 않습니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,"이미 존재하는 아이디입니다."),
    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST,"잘못된 접근 입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰입니다.");

    private HttpStatus httpStatus;
    private String message;
}
