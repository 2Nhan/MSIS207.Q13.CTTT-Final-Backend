package com.crm.project.service;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MailtrapService {
    private String mailtrapToken = "40eaaa0614f2ca5877ac0d90ffc2be69";
    private MailtrapConfig config = new MailtrapConfig.Builder().token(mailtrapToken).build();
    private MailtrapClient client = MailtrapClientFactory.createMailtrapClient(config);


    public void sendResultEmail() {
        String emailBody = "Hello World";

        MailtrapMail mail = MailtrapMail.builder()
                .from(new Address("letunhan137@demomailtrap.co"))
                .to(List.of(new Address("letunhan137@gmail.com")))
                .subject("Hello World!")
                .html(emailBody)
                .category("Test")
                .build();
        try {
            client.send(mail);
        } catch (Exception e) {
            System.err.println("Mailtrap API error for auditId: " + "error message:" + e.getMessage());
        }
    }
}

