package com.est.gongmoja.service;

import com.est.gongmoja.entity.UserEntity;
import com.est.gongmoja.exception.CustomException;
import com.est.gongmoja.exception.ErrorCode;
import com.est.gongmoja.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;




    //메일 전송 메서드
    public void sendMail(String email){
        //유저 객체 생성
        UserEntity userEntity = getUserByEmail(email);

        //임시비밀번호 생성
        String tempPassword = createTempPassword();


        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,false,"UTF-8");

            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("[공모자들] 임시 비밀번호 발급");
            mimeMessageHelper.setText(setContext(tempPassword),true);

            javaMailSender.send(mimeMessage);

            //서버 내에 유저 비밀번호 교체
            updateTempPassword(userEntity,tempPassword);
        }
        catch(MessagingException e){
            throw new RuntimeException(e);
        }
    }



    //유효한 메일 ( 서버내에 저장된 메일인지 ) 확인하는 메서드
    private UserEntity getUserByEmail(String email){
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        return optionalUser.get();
    }

    //임시 비밀번호 생성 메서드
    private String createTempPassword(){
        return UUID.randomUUID().toString().replace("-","");
    }

    //User 비밀번호 변경 메서드
    private void updateTempPassword(UserEntity user,String password){
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private String setContext(String tempPassword){
        Context context = new Context();
        context.setVariable("tempPassword",tempPassword);
        return springTemplateEngine.process("temp-password" ,context);
    }
}
