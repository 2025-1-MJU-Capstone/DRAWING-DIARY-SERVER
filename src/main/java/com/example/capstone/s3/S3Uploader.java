package com.example.capstone.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String upload(byte[] fileData, String fileName) {

        String contentType;
        String contentDisposition;

        if (fileName.endsWith(".ttf")) {
            contentType = "font/ttf";
            contentDisposition = "attachment";
        } else if (fileName.endsWith(".png")) {
            contentType = "image/png";
            contentDisposition = "inline";
        } else {
            contentType = "application/octet-stream";
            contentDisposition = "attachment";
        }

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
//                .acl("public-read")
                .contentType(contentType)
                .contentDisposition(contentDisposition)
//                .cacheControl("max-age=31536000")
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(fileData));

        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    public void delete(String fileName) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    public void deleteAllByMemberId(Long memberId) {
        String prefix = memberId + "/";
        List<ObjectIdentifier> allObjectsToDelete = new ArrayList<>();

        String continuationToken = null;

        do {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(prefix)
                    .continuationToken(continuationToken)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            List<S3Object> objects = listResponse.contents();
            if (objects.isEmpty()) break;

            for (S3Object s3Object : objects) {
                allObjectsToDelete.add(ObjectIdentifier.builder().key(s3Object.key()).build());
            }

            continuationToken = listResponse.isTruncated() ? listResponse.nextContinuationToken() : null;
        } while (continuationToken != null);

        // 한 번에 삭제 (최대 1000개까지 가능, 초과시 나눠서 호출)
        for (int i = 0; i < allObjectsToDelete.size(); i += 1000) {
            int end = Math.min(i + 1000, allObjectsToDelete.size());
            List<ObjectIdentifier> chunk = allObjectsToDelete.subList(i, end);

            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(Delete.builder().objects(chunk).build())
                    .build();

            s3Client.deleteObjects(deleteRequest);
        }
    }

    public String getUrl(String fileName) {
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
    }
}
