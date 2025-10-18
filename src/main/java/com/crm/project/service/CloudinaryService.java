package com.crm.project.service;

import com.cloudinary.Cloudinary;
import com.crm.project.dto.response.CloudinaryResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@RequiredArgsConstructor
@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryResponse uploadFile(MultipartFile file, String fileName) {
        try {
            String fileBaseName = FilenameUtils.getBaseName(fileName);
            Map result = cloudinary.uploader().upload(file.getBytes(), Map.of("public_id", "project/images/" + fileBaseName));
            String url = result.get("secure_url").toString();
            String publicId = result.get("public_id").toString();
            return CloudinaryResponse.builder()
                    .publicId(publicId)
                    .url(url)
                    .build();
        }
        catch (IOException exception) {
            throw new RuntimeException("Failed to upload file" ,exception);
        }
    }
}
