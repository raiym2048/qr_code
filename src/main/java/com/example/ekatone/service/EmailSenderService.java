package com.example.ekatone.service;

public interface EmailSenderService {
    void sendEmail(String toEmail, String subject, String body, int code);
}
