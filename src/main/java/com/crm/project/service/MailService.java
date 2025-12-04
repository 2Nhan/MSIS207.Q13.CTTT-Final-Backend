package com.crm.project.service;

import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.sender}")
    private String sender;

    private Resend resend;

    @PostConstruct
    public void init() {
        this.resend = new Resend(apiKey);
    }

    public void sendMail(String to) {
        Resend resend = new Resend(apiKey);

        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(sender)
                .to(to)
                .subject("Bao gia")
                .text("Day la bao gia cho")
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(email);
        } catch (ResendException e) {
            throw new AppException(ErrorCode.MAILING_FAILED);
        }
    }

    public void sendQuotationMail(String to, String fullName, List<String> products) {
        String subject = "Báo giá cho danh mục sản phẩm: " + products.toString();
        String text = "Kính gửi: Anh/Chị " + fullName
                + "\nLời đầu tiên, chúng tôi xin cảm ơn Quý khách hàng đã quan tâm đến sản phẩm của chúng tôi."
                + "\nTiếp nhận yêu cầu của Anh/Chị, tôi xin gửi kèm email này bảng báo giá chi tiết cho hạng mục"
                + products.toString()
                + "Trong file đính kèm, chúng tôi đã liệt kê rõ các thông số kỹ thuật, đơn giá và các chính sách ưu đãi dành riêng cho Quý khách hàng trong tháng này."
                + "\nAnh/Chị vui lòng xem file PDF đính kèm để có thông tin đầy đủ nhất."
                + "\nMong sớm nhận được phản hồi từ Anh/Chị"
                + "\nTrân trọng,"
                + "\nNhân";

        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(sender)
                .to(to)
                .subject(subject)
                .text(text)
                .build();
        try {
            CreateEmailResponse response = resend.emails().send(email);
        } catch (ResendException e) {
            throw new AppException(ErrorCode.MAILING_FAILED);
        }
    }


}