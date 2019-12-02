package com.aws.samples.petclinic.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Singleton;

@Configuration
public class AWSConfig {

    @Bean
    public boolean enableXRay() {
        return System.getenv("AWS_XRAY_DAEMON_ADDRESS") != null;
    }

    @Bean(name = "petsTableName")
    public String petsTableName() {
        return System.getenv("TABLE_NAME");
    }

    @Bean(name = "petsBucketName")
    public String petsBucketName() {
        return System.getenv("BUCKET_NAME");
    }

    @Bean
    public Regions provideRegions() {
        return Regions.fromName(System.getenv("AWS_REGION"));
    }

    @Bean(name = "awsCredentialsProvider")
    public AWSCredentialsProvider amazonAWSCredentialsProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Bean
    public TracingHandler provideTracingHandler(boolean enableXRay) {
        return new TracingHandler(AWSXRay.getGlobalRecorder());
    }

    @Bean(name = "amazonDynamoDB")
    public AmazonDynamoDB amazonDynamoDB(TracingHandler tracingHandler, boolean enableXRay, Regions regions, AWSCredentialsProvider credentialsProvider) {
        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(regions);

        if (enableXRay) {
            builder.withRequestHandlers(tracingHandler);
        }

        return builder.build();
    }

    @Bean(name = "s3")
    public AmazonS3 amazonS3(TracingHandler tracingHandler, boolean enableXRay, Regions regions, AWSCredentialsProvider credentialsProvider) {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(regions);

        if (enableXRay) {
            builder.withRequestHandlers(tracingHandler);
        }

        return builder.build();
    }
}
