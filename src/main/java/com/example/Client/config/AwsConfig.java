package com.example.Client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    private StaticCredentialsProvider createCredentialsProvider(){
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return StaticCredentialsProvider.create(credentials);
    }

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(createCredentialsProvider())
                .build();
    }

    @Bean
    public SnsClient snsClient(){
        return SnsClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(createCredentialsProvider())
                .build();
    }
}
