package com.est.gongmoja.oauth2;

import com.est.gongmoja.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler {
    private final UserRepository userRepository;


}
