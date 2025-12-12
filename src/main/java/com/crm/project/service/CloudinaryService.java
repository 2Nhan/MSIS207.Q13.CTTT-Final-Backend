package com.crm.project.service;

import com.cloudinary.Cloudinary;
import com.crm.project.internal.CloudinaryInfo;
import com.crm.project.repository.QuotationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    private final QuotationRepository quotationRepository;

    public CloudinaryInfo uploadFile(MultipartFile file, String fileName) {
        try {
            String fileBaseName = FilenameUtils.getBaseName(fileName);
            Map result = cloudinary.uploader().upload(file.getBytes(), Map.of("public_id", "project/images/" + fileBaseName));
            String url = result.get("secure_url").toString();
            String publicId = result.get("public_id").toString();
            return CloudinaryInfo.builder()
                    .publicId(publicId)
                    .url(url)
                    .build();
        } catch (IOException exception) {
            throw new RuntimeException("Failed to upload file", exception);
        }
    }

    private CloudinaryInfo uploadGeneratedFile(byte[] fileBytes, String fileName, String folder) {
        try {
            String fileBaseName = FilenameUtils.getBaseName(fileName);

            // "resource_type: auto" => cho phép Cloudinary nhận PDF, image, video, raw
            Map<?, ?> result = cloudinary.uploader().upload(fileBytes, Map.of(
                    "public_id", folder + "/" + fileBaseName,
                    "resource_type", "auto"
            ));

            String url = result.get("secure_url").toString();
            String publicId = result.get("public_id").toString();

            return CloudinaryInfo.builder()
                    .publicId(publicId)
                    .url(url)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload generated file", e);
        }
    }


    @Transactional
    public void uploadAndUpdateRecord(byte[] fileBytes, String fileName, String folder, String quotationId) {
        try {
            CloudinaryInfo info = uploadGeneratedFile(fileBytes, fileName, folder);
            quotationRepository.updateFilePath(quotationId, info.getUrl());
            log.info("Updated filePath for quotation [{}] -> {}", quotationId, info.getUrl());
        } catch (Exception e) {
            log.error("Failed to upload or update record [{}]: {}", quotationId, e.getMessage());
        }
    }
}
