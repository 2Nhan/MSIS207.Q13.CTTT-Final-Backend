package com.crm.project.service;

import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.sender}")
    private String sender;

    public void sendMail(String to) {
        Resend resend = new Resend(apiKey);

        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(sender)
                .to(to)
                .subject("Hello from spring boot sender")
                .text("Xin chào từ Spring Boot! Đây là email test qua Resend API.")
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(email);
        } catch (ResendException e) {
            throw new AppException(ErrorCode.MAILING_FAILED);
        }
    }
}