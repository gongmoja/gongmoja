package com.est.gongmoja.oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory{

    public static OAuth2UserInfo getOAuth2UserInfo(String authProvider, Map<String, Object> attributes){
        if(authProvider.equals("kakao")) return new KakaoOAuth2User(attributes);
        else if(authProvider.equals("naver")) return new NaverOAuth2User(attributes);
        else throw new IllegalArgumentException();
    }
}
