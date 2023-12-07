package com.example.ekatone.service.impl;

import com.example.ekatone.service.EmailSenderService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String toEmail, String subject, String body, int code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("devsfactoryBack@gmail.com");
        message.setTo(toEmail);
        message.setText(body+"\n The refresh code: "+ code);
        message.setSubject(subject);
        mailSender.send(message);
    }
}
