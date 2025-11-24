package com.crm.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendTestMail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("✅ Test gửi mail từ Spring Boot");
        message.setText("Xin chào!\n\nĐây là email test gửi từ ứng dụng Spring Boot deploy lên Render.\n\nTrân trọng!");
        message.setFrom("no-reply@yourapp.com");

        mailSender.send(message);
        System.out.println("✅ Mail sent successfully to " + to);
    }
}