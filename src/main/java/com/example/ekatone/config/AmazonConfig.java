package com.example.ekatone.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AmazonConfig {
    @Value("${aws.access.key}")
    String accessKey;
    @Value("${aws.secret.access.key}")
    String accessSecretKey;

    @Bean(name = "amazonS3")
    public AmazonS3 s3Bucket() {
        AWSCredentials awsCredentials =
                new BasicAWSCredentials(accessKey, accessSecretKey);
        return AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-2")
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }
}
