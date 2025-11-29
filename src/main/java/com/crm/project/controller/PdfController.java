package com.crm.project.controller;

import com.crm.project.dto.response.ProductResponse;
import com.crm.project.entity.Product;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.ProductMapper;
import com.crm.project.repository.ProductRepository;
import com.crm.project.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pdf")
public class PdfController {
    private final PdfService pdfService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateQuotation(@RequestParam String receiver,
                                                    @RequestParam String sender,
                                                    @RequestParam String startDate,
                                                    @RequestParam String endDate) {
        try {
            Product product = productRepository.findById("a4cc9800-29f5-4059-81c1-918791fc1382").orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            ProductResponse productResponse = productMapper.toProductResponse(product);
            byte[] pdfBytes = pdfService.generateQuotation(
                    receiver,
                    sender,
                    startDate,
                    endDate,
                    List.of(productResponse)
            );

            // Trả về file PDF trực tiếp cho người dùng tải/xem
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=quotation.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .header("Error-Message", e.getMessage())
                    .body(null);
        }
    }
}
