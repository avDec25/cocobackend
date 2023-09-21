package com.cocosorority.cocobackend.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioClientConfig {

    @Value("${minio.access-key}")
    String ACCESS_KEY;

    @Value("${minio.access-secret}")
    String ACCESS_SECRET;

    @Value("${minio.endpoint}")
    String ENDPOINT;

    @Bean
    MinioClient getMinioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, ACCESS_SECRET)
                .build();

        return minioClient;
    }

}
