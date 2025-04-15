package com.example.Client.minio;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinIOConfig {

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint("http://127.0.0.1:9000") // Укажите ваш хост MinIO
                .credentials("wq1FMlOUXTmzGooMYt94", "ljtkeKVTSgf4BLPKiWKIT14U304sGyvbGLQAMVg7") // Укажите ваши креденшалы
                .build();
    }
}
