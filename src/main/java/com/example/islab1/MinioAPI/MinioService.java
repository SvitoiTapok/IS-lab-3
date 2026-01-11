package com.example.islab1.MinioAPI;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public String saveImportFile(MultipartFile file) throws Exception {
        ensureBucketExists();

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uniqueFileName = String.format("import_%s_%s%s",
                timestamp, UUID.randomUUID().toString().substring(0, 8), fileExtension);

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("Файл '{}' сохранен в MinIO как '{}'", originalFilename, uniqueFileName);
            return uniqueFileName;
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        if (!bucketExists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            log.info("Bucket '{}' создан в MinIO", bucketName);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public StatObjectResponse getFileMetadata(String fileName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    public ResponseEntity<Resource> downloadFileAsAttachment(String fileName, String originalFilename) throws Exception {
        var stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );

        var stat = minioClient.statObject(
                io.minio.StatObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );

        InputStreamResource resource = new InputStreamResource(stream);
        String cleanFilename = originalFilename.trim();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + cleanFilename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(stat.size())
                .body(resource);
    }

    public void removeFile(String fileName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket("import-files").object(fileName).build());
    }


    public boolean fileExists(String fileName) {
        try {
            getFileMetadata(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}