package com.crm.project.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import java.util.HashMap;

@Configuration
public class CloudinaryConfiguration {
    @Value("${cloudinary.cloudinary-name}")
    private String cloudinaryName;

    @Value("${cloudinary.cloudinary-api-key}")
    private String apiKey;

    @Value("${cloudinary.cloudinary-api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        final Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinaryName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}
