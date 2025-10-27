package com.crm.project.controller;

import com.cloudinary.Api;
import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@RequestPart(value = "product") ProductCreationRequest request,
                                                     @RequestPart(value = "image", required = false) MultipartFile image) {
        ProductResponse productResponse = productService.createProduct(request, image);

        ApiResponse apiResponse = ApiResponse.builder()
                .result(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
