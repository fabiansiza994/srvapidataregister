package com.fmsp.srvapidataregister.modules.S3.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(String prefix, byte[] bytes, String contentType) {
        String ct = (contentType != null && !contentType.isBlank())
                ? contentType.toLowerCase()
                : "application/octet-stream";

        String extension = resolveExtension(ct);
        String fileName = prefix + UUID.randomUUID() + extension;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(ct)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }

    private String resolveExtension(String contentType) {
        if (contentType == null) {
            return ".bin";
        }
        // Imágenes
        if (contentType.startsWith("image/")) {
            if (contentType.equals("image/jpeg") || contentType.equals("image/jpg")) return ".jpg";
            if (contentType.equals("image/png")) return ".png";
            if (contentType.equals("image/gif")) return ".gif";
            if (contentType.equals("image/webp")) return ".webp";
            return ".img";
        }
        // PDF
        if (contentType.equals("application/pdf")) return ".pdf";
        // Word
        if (contentType.equals("application/msword")) return ".doc";
        if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) return ".docx";
        // Excel (por si acaso)
        if (contentType.equals("application/vnd.ms-excel")) return ".xls";
        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) return ".xlsx";

        // Por defecto, binario genérico
        return ".bin";
    }

    public void deleteFile(String fileUrl) {
        String key = fileUrl.replace("https://" + bucketName + ".s3.amazonaws.com/", "");
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

}
