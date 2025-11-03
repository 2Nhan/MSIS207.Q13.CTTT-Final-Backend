package com.crm.project.controller;

import com.cloudinary.Api;
import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.response.*;
import com.crm.project.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse> createProduct(@RequestPart(value = "product") @Valid ProductCreationRequest request,
                                                     @RequestPart(value = "image", required = false) MultipartFile image) {
        ProductResponse productResponse = productService.createProduct(request, image);

        ApiResponse apiResponse = ApiResponse.builder()
                .result(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/csv")
    public ResponseEntity<ApiResponse> importProductsFromCsv(@RequestParam(value = "csv") MultipartFile file) throws IOException {
        ImportPreviewResponse previewResponse = productService.importProductsFromCsv(file);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(previewResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/list")
    public ResponseEntity<ApiResponse> createListOfProducts(@RequestBody @Valid List<ProductCreationRequest> requests) {
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

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts(@RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                      @RequestParam(required = false, defaultValue = "sku") String sortBy,
                                                      @RequestParam(required = false, defaultValue = "asc") String sortOrder) {
        Page<ProductResponse> productResponseList = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder);

        ApiResponse apiResponse = ApiResponse.builder()
                .result(new PageResponse<>(productResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse> uploadProductImage(@PathVariable("id") String id, @RequestPart("image") MultipartFile file) {
        ImageResponse imageResponse = productService.uploadProductImage(id, file);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(imageResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable("id") String id, @RequestBody @Valid ProductCreationRequest request) {
        ProductResponse productResponse = productService.updateProduct(id, request);
        ApiResponse apiResponse = ApiResponse.builder()
                .result(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        ApiResponse apiResponse = ApiResponse.builder()
                .message("PRODUCT DELETED")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
