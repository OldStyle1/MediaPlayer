package com.example.Client.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class MinIOService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinIOService(MinioClient minioClient) {
        this.minioClient = minioClient;
        this.bucketName = "saveaudio"; // Укажите ваше имя бакета
    }

    // Загрузка файла в MinIO
    public String uploadFile(MultipartFile file) throws IOException {
        String objectName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            // Log the exception or handle it as per your application's error strategy
            throw new IOException("An error occurred while uploading file to MinIO", e);
        }

        return objectName; // Return the object name which will be stored in the database
    }

    // Удаление файла из MinIO
    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла из MinIO", e);
        }
    }

    // Получение URL файла из MinIO
    public String getFileUrl(String objectName) {
        return String.format("/%s/%s", bucketName, objectName);
    }
}
