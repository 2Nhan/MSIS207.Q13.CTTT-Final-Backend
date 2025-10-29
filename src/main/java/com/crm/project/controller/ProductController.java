package com.crm.project.controller;

import com.cloudinary.Api;
import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.response.ApiResponse;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
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

    @PostMapping("/list")
    public ResponseEntity<ApiResponse> createListOfProducts(@RequestPart("products") List<ProductCreationRequest> requests) {
        List<ProductResponse> productResponses = productService.createListOfProducts(requests);

        ApiResponse apiResponse = ApiResponse.builder()
                .result(productResponses)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProduct(@PathVariable("id") String id) {
        ProductResponse productResponse = productService.getProduct(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("Product deleted")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
