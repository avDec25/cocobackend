package com.cocosorority.cocobackend.item;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.SneakyThrows;

@Service
public class ItemService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MinioClient minioClient;

    private final String SQL_SAVE_ITEM = "INSERT INTO items (name, drop_id, cost, shipping, image) VALUES (?, ?, ?, ?, ?)";

    @SneakyThrows
    public String saveItem(ItemSaveRequest request) {
        System.out.println("Image size: " + request.image.getSize());
        
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(request.dropId).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(request.dropId).build());
        }

        String filepath = String.format("/tmp/cocoimages/%s", request.image.getOriginalFilename());
        FileUtils.writeByteArrayToFile(
            new File(filepath), 
            request.image.getBytes()
        );
        

        minioClient.uploadObject(
          UploadObjectArgs.builder()
              .bucket(request.dropId)
              .object(request.image.getOriginalFilename())
              .filename(filepath)
              .build());


        int updatedRows = jdbcTemplate.update(
            SQL_SAVE_ITEM,
            request.name,
            request.dropId,
            request.cost,
            request.shipping,
            request.image.getOriginalFilename()
        );
        return String.format("Updated %d rows", updatedRows);
    }

}
