package com.cocosorority.cocobackend.item;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import io.minio.UploadObjectArgs;
import lombok.SneakyThrows;

@Service
public class ItemService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MinioClient minioClient;

    String SQL_SELECT_ITEMS = "SELECT item_id, name, drop_id, image, cost_price, selling_price, shipping FROM items WHERE drop_id=?";
    
    private final String SQL_SAVE_ITEM = "INSERT INTO items (name, drop_id, cost_price, selling_price, shipping, image) VALUES (?, ?, ?, ?, ?, ?)";
    private final String PUBLIC_POLICY = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetBucketLocation\",\"s3:ListBucket\",\"s3:ListBucketMultipartUploads\"],\"Resource\":[\"arn:aws:s3:::%s\"]},{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\",\"s3:ListMultipartUploadParts\"],\"Resource\":[\"arn:aws:s3:::%s/*\"]}]}";
    

    @SneakyThrows
    public String saveItem(ItemSaveRequest request) {        
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(request.dropId).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(request.dropId).build());
            minioClient.setBucketPolicy(
                SetBucketPolicyArgs.builder()
                .bucket(request.dropId)
                .config(
                    String.format(PUBLIC_POLICY, request.dropId, request.dropId)
                )
                .build()
            );
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
            request.costPrice,
            request.sellingPrice,
            request.shipping,
            request.image.getOriginalFilename()
        );
        return String.format("Updated %d rows", updatedRows);
    }


    @SneakyThrows
    public List<ItemQuickView> fetchFilteredListWithDropId(String dropId) {
        PreparedStatementSetter setter = new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, dropId);
            }
        };
        List<ItemQuickView> data = jdbcTemplate.query(
            SQL_SELECT_ITEMS,
            setter,
            new ResultSetExtractor<List<ItemQuickView>>() {
                @Override
                public List<ItemQuickView> extractData(ResultSet rs) throws SQLException {
                    List<ItemQuickView> result = new ArrayList<ItemQuickView>();
                    while(rs.next()) {
                        ItemQuickView qView = new ItemQuickView();
                        qView.itemId = rs.getString(1);
                        qView.name = rs.getString(2);
                        qView.dropId = rs.getString(3);
                        qView.image = rs.getString(4);
                        qView.costPrice = rs.getString(5);
                        qView.sellingPrice = rs.getString(6);
                        qView.shipping = rs.getString(7);
                        result.add(qView);
                    }
                    return result;
                }
            }
        );
        return data;
    }
}
