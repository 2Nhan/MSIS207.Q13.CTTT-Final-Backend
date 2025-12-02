package com.crm.project.controller;

import com.crm.project.dto.request.ImageUploadRequest;
import com.crm.project.dto.request.MatchingRequest;
import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.request.ProductUpdateRequest;
import com.crm.project.dto.response.*;
import com.crm.project.internal.PageInfo;
import com.crm.project.service.ProductService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<MyApiResponse> createProduct(@ModelAttribute @Valid ProductCreationRequest request) {
        ProductResponse productResponse = productService.createProduct(request);

        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MyApiResponse> importProductsFromCsv(
            @RequestPart("matching") MatchingRequest matching,
            @RequestPart("file") MultipartFile file) throws IOException {
        ImportResultResponse<ProductResponse> productResponses = productService.importProductsFromCsv(matching, file);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponses.getValidList())
                .error(productResponses.getInvalidList())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyApiResponse> getProduct(@PathVariable("id") String id) {
        ProductResponse productResponse = productService.getProduct(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<MyApiResponse> getAllProducts(@RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                        @RequestParam(required = false, defaultValue = "sku") String sortBy,
                                                        @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                                        @RequestParam(required = false) String category,
                                                        @RequestParam(required = false) String status,
                                                        @RequestParam(required = false) BigDecimal minPrice,
                                                        @RequestParam(required = false) BigDecimal maxPrice) {
        Page<ProductResponse> productResponseList = productService.getAllProducts(pageNumber, pageSize, sortBy, sortOrder, category, status, minPrice, maxPrice);

        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponseList.getContent())
                .pagination(new PageInfo<>(productResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<MyApiResponse> searchProducts(@RequestParam(name = "query") String query,
                                                        @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNumber,
                                                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Page<ProductResponse> productResponseList = productService.searchProducts(query, PageRequest.of(pageNumber - 1, pageSize));
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponseList.getContent())
                .pagination(new PageInfo<>(productResponseList))
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MyApiResponse> updateProduct(@PathVariable("id") String id, @ModelAttribute @Valid ProductUpdateRequest request) {
        ProductResponse productResponse = productService.updateProduct(id, request);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .data(productResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MyApiResponse> deleteProduct(@PathVariable("id") String id) {
        productService.deleteProduct(id);
        MyApiResponse apiResponse = MyApiResponse.builder()
                .message("Product deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
