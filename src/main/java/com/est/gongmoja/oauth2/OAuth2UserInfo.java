package com.est.gongmoja.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public abstract class OAuth2UserInfo {
    //속성
    protected Map<String, Object> attributes;

    //OAuth2 Id
    public abstract String getOAuth2Id();

    //Email
    public abstract String getEmail();

    //이름
    public abstract String getName();


}
