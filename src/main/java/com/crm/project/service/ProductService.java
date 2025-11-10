package com.crm.project.service;

import com.crm.project.dto.request.ProductCreationRequest;
import com.crm.project.dto.response.CloudinaryResponse;
import com.crm.project.dto.response.ImageResponse;
import com.crm.project.dto.response.ImportPreviewResponse;
import com.crm.project.dto.response.ProductResponse;
import com.crm.project.entity.Product;
import com.crm.project.exception.AppException;
import com.crm.project.exception.ErrorCode;
import com.crm.project.mapper.ProductMapper;
import com.crm.project.repository.ProductRepository;
import com.crm.project.utils.FileUploadUtil;
import com.crm.project.utils.SystemHeaderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final CsvService csvService;
    private final ExcelService excelService;

    public ProductResponse createProduct(ProductCreationRequest request, MultipartFile image) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new AppException(ErrorCode.PRODUCT_SKU_EXISTED, "sku");
        }
        Product product = productMapper.toProduct(request);
        if (image != null || !image.isEmpty()) {
            String filename = FileUploadUtil.standardizeFileName(image.getOriginalFilename());
            FileUploadUtil.checkImage(image, FileUploadUtil.IMAGE_PATTERN);
            CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(image, filename);

            product.setImageUrl(cloudinaryResponse.getUrl());
        }

        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public ImportPreviewResponse importProductsFromCsv(MultipartFile file) throws IOException {
        ImportPreviewResponse importPreviewResponse = csvService.parseCsvFile(file);
        importPreviewResponse.setSystemHeader(SystemHeaderUtil.getSystemHeaders(ProductCreationRequest.class));
        return importPreviewResponse;
    }

    public ImportPreviewResponse importProductsFromExcel(MultipartFile file) throws IOException {
        ImportPreviewResponse importPreviewResponse = excelService.parseExcelFile(file);
        importPreviewResponse.setSystemHeader(SystemHeaderUtil.getSystemHeaders(ProductCreationRequest.class));
        return importPreviewResponse;
    }

    public List<ProductResponse> createListOfProducts(List<ProductCreationRequest> requests) {
        List<String> skus = requests.stream().map(ProductCreationRequest::getSku).toList();
        if (productRepository.existsBySkuIn(skus)) {
            throw new AppException(ErrorCode.PRODUCT_SKU_EXISTED);
        }

        List<Product> products = requests.stream().map(productMapper::toProduct).toList();
        productRepository.saveAll(products);
        return products.stream().map(productMapper::toProductResponse).toList();
    }

    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    public Page<ProductResponse> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Page<Product> products = productRepository.findAll(pageable);

        if (products.isEmpty()) {
            throw new AppException(ErrorCode.NO_RESULTS);
        }
        return products.map(productMapper::toProductResponse);
    }

    public ImageResponse uploadProductImage(String id, MultipartFile file) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        FileUploadUtil.checkImage(file, FileUploadUtil.IMAGE_PATTERN);
        String filename = FileUploadUtil.standardizeFileName(file.getOriginalFilename());
        CloudinaryResponse cloudinaryResponse = cloudinaryService.uploadFile(file, filename);
        product.setImageUrl(cloudinaryResponse.getUrl());
        productRepository.save(product);
        return ImageResponse.builder().url(cloudinaryResponse.getUrl()).build();
    }

    public ProductResponse updateProduct(String id, ProductCreationRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productMapper.updateProduct(request, product);
        productRepository.save(product);
        return productMapper.toProductResponse(product);
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setDeleted(true);
        productRepository.save(product);
    }
}
