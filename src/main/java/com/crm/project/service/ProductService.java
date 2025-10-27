package com.crm.project.service;

import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.response.CloudinaryResponse;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.entity.Product;
import com.crm.project.mapper.ProductMapper;
import com.crm.project.repository.ProductRepository;
import com.crm.project.utils.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;

    public ProductResponse createProduct(ProductCreationRequest request, MultipartFile image) {
        Product product = productMapper.toProduct(request);

        String filename = UploadFileUtil.standardizeFileName(image.getOriginalFilename());
        UploadFileUtil.assertAllowed(image, UploadFileUtil.IMAGE_PATTERN);
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(image, filename);

        product.setImageUrl(cloudinaryResponse.getUrl());
        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

}
