package com.crm.project.service;

import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.sender}")
    private String from;

    private Resend resend;

    @PostConstruct
    public void init() {
        this.resend = new Resend(apiKey);
    }


    public void sendQuotationMail(
            String to,
            String sender,
            String fullName,
            List<String> products,
            byte[] attachment
    ) throws Exception {

        String subject = "Báo giá cho danh mục sản phẩm: " + String.join(", ", products);

        String text = "Kính gửi: Anh/Chị " + fullName
                + "\n\nLời đầu tiên, chúng tôi xin cảm ơn Quý khách hàng đã quan tâm đến sản phẩm của chúng tôi."
                + "\n\nTiếp nhận yêu cầu của Anh/Chị, tôi xin gửi kèm email này bảng báo giá chi tiết cho hạng mục: "
                + String.join(", ", products) + "."
                + "\n\nTrong file đính kèm, chúng tôi đã liệt kê rõ các thông số kỹ thuật, đơn giá và chính sách ưu đãi."
                + "\n\nAnh/Chị vui lòng xem file PDF đính kèm để có thông tin đầy đủ nhất."
                + "\n\nMong sớm nhận được phản hồi từ Anh/Chị."
                + "\n\nTrân trọng,"
                + "\n" + sender;

        Attachment pdfAttachment = Attachment.builder()
                .fileName("Quotation.pdf")
                .content(Base64.getEncoder().encodeToString(attachment))
                .build();

        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .text(text)
                .addAttachment(pdfAttachment)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(email);
        } catch (ResendException e) {
            throw new AppException(ErrorCode.MAILING_FAILED);
        }
    }
}