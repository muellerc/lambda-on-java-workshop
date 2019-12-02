package com.aws.samples.petclinic.config;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    @Bean(name = "petsTableName")
    public String petsTableName() {
        return System.getenv("TABLE_NAME");
    }

    @Bean(name = "petsBucketName")
    public String petsBucketName() {
        return System.getenv("BUCKET_NAME");
    }

    @Bean(name = "awsCredentialsProvider")
    public AWSCredentialsProvider amazonAWSCredentialsProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Bean
    public Regions provideRegions() {
        return Regions.fromName(System.getenv("AWS_REGION"));
    }

    @Bean(name = "amazonDynamoDB")
    public AmazonDynamoDB amazonDynamoDB(AWSCredentialsProvider credentialsProvider, Regions regions) {
        return AmazonDynamoDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(regions)
                .build();
    }

    @Bean(name = "s3")
    public AmazonS3 amazonS3(AWSCredentialsProvider credentialsProvider, Regions regions) {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(regions)
                .build();
    }
}
