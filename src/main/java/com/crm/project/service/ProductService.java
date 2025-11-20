package com.crm.project.service;

import com.crm.project.dto.request.MatchingRequest;
import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.request.ProductUpdateRequest;
import com.crm.project.dto.response.*;
import com.crm.project.entity.Product;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.ProductMapper;
import com.crm.project.repository.ProductRepository;
import com.crm.project.utils.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.ArrayList;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final CsvService csvService;


    public ProductResponse createProduct(ProductCreationRequest request, MultipartFile image) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new AppException(ErrorCode.PRODUCT_SKU_EXISTED, "sku");
        }
        Product product = productMapper.toProduct(request);
        if (image != null && !image.isEmpty()) {
            String filename = FileUploadUtil.standardizeFileName(image.getOriginalFilename());
            FileUploadUtil.checkImage(image, FileUploadUtil.IMAGE_PATTERN);
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(image, filename);

            product.setImageUrl(cloudinaryResponse.getUrl());
        }

        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public ImportResultResponse<ProductResponse> importProductsFromCsv(
            MatchingRequest matching,
            MultipartFile file) throws IOException {

        ImportResponse importResponse = csvService.parseCsvFile(matching.getMatching(), file);

        List<Product> products = importResponse.getData()
                .stream()
                .map(productMapper::importToProduct)
                .toList();

        List<Product> invalidList = new ArrayList<>();
        List<Product> tempList = new ArrayList<>();
        List<Product> validList = new ArrayList<>();

        for (Product p : products) {
            boolean invalid =
                    isBlank(p.getSku()) ||
                            isBlank(p.getName()) ||
                            isBlank(p.getCategory()) ||
                            isBlank(p.getStatus()) ||
                            p.getPrice() == null;

            if (invalid) {
                invalidList.add(p);
            } else {
                tempList.add(p);
            }
        }

        if (tempList.isEmpty()) {
            return ImportResultResponse.<ProductResponse>builder()
                    .validList(List.of())
                    .invalidList(invalidList.stream().map(productMapper::toProductResponse).toList())
                    .build();
        }


        List<String> tempSkus = tempList.stream()
                .map(Product::getSku)
                .filter(s -> s != null && !s.trim().isEmpty())
                .toList();

        Set<String> duplicatedSkus = productRepository.findBySkuIn(tempSkus);
        for (Product p : tempList) {
            if (duplicatedSkus.contains(p.getSku())) {
                invalidList.add(p);
            } else {
                validList.add(p);
                duplicatedSkus.add(p.getSku());
            }
        }


        if (!validList.isEmpty()) {
//            productRepository.saveAll(validList);
        }

        return ImportResultResponse.<ProductResponse>builder()
                .validList(validList.stream().map(productMapper::toProductResponse).toList())
                .invalidList(invalidList.stream().map(productMapper::toProductResponse).toList())
                .build();
    }


    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    public Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder,
                                                String category, String status, BigDecimal minPrice, BigDecimal maxPrice) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);

        Specification<Product> spec = Specification.allOf();

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
        }

        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        Page<Product> products = productRepository.findAll(spec, pageable);

        if (products.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return products.map(productMapper::toProductResponse);
    }

    public Page<ProductResponse> searchProducts(String query, Pageable pageable) {
        Page<Product> products = productRepository.findBySearch(query, pageable);
        if (products.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return products.map(productMapper::toProductResponse);
    }

    public ImageResponse uploadProductImage(String id, MultipartFile file) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.MISSING_FILE);
        }
        FileUploadUtil.checkImage(file, FileUploadUtil.IMAGE_PATTERN);
        String filename = FileUploadUtil.standardizeFileName(file.getOriginalFilename());
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(file, filename);
        product.setImageUrl(cloudinaryResponse.getUrl());
        productRepository.save(product);
        return ImageResponse.builder().url(cloudinaryResponse.getUrl()).build();
    }

    public ProductResponse updateProduct(String id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (productRepository.existsBySku(request.getSku())) {
            throw new AppException(ErrorCode.PRODUCT_SKU_EXISTED);
        }
        productMapper.updateProduct(request, product);
        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setDeleted(true);
        productRepository.save(product);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
