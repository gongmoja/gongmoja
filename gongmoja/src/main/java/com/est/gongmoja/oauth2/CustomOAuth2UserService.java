package com.est.gongmoja.oauth2;

import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.repository.UserRepository;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.AuthProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        //oauth user 객체 생성
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        return processOAuth2User(userRequest,oAuth2User);
    }

    protected OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User){
        //제공자 (카카오 or 네이버)
        String authProvider = userRequest.getClientRegistration().getRegistrationId().toLowerCase();

        //user 정보 객체 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(authProvider,oAuth2User.getAttributes());

        //db 저장 단계
        //OAuth2 속성에 이메일이 담겨오지 않았다면 예외처리
        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) throw new RuntimeException();

        //User 객체 저장 , 생성
        UserEntity userEntity = saveOrUpdateUser(authProvider,oAuth2UserInfo);

        return new CustomOAuth2User(
                oAuth2User.getAttributes(),
                Collections.singletonList(new SimpleGrantedAuthority("USER")),
                userEntity.getEmail());
    }

    private UserEntity saveOrUpdateUser(String authProvider, OAuth2UserInfo oAuth2UserInfo){
        UserEntity user = UserEntity
                .builder()
                .userName(oAuth2UserInfo.getEmail()) //email 과 username 같게 설정함
                .email(oAuth2UserInfo.getEmail()) //email 과 username 같게 설정함
                .provider(authProvider)
                .nickName(oAuth2UserInfo.getName())
                .providerId(oAuth2UserInfo.getOAuth2Id())
                .role(1)
                .build();

        return userRepository.save(user);
    }

}
