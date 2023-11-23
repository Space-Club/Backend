package com.spaceclub.global.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.spaceclub.global.config.s3.properties.S3IamProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3IamProperties iamProperties;

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(iamProperties.accessKey(), iamProperties.secretKey());

        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withRegion(iamProperties.region())
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }

}
