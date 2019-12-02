package com.aws.samples.petclinic.dagger;

import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.handlers.TracingHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Nullable;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

@Module
public class PetModule {

    @Provides
    @Singleton
    public boolean enableXRay() {
        return System.getenv("AWS_XRAY_DAEMON_ADDRESS") != null;
    }

    @Provides
    @Singleton
    @Named(value = "petsTableName")
    public String petsTableName() {
        return System.getenv("TABLE_NAME");
    }

    @Provides
    @Singleton
    @Named(value = "petsBucketName")
    public String petsBucketName() {
        return System.getenv("BUCKET_NAME");
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return new ObjectMapper();
    }

    @Provides
    @Singleton
    public Regions provideRegions() {
        // https://docs.aws.amazon.com/lambda/latest/dg/lambda-environment-variables.html
        return Regions.fromName(System.getenv("AWS_REGION"));
    }

    @Provides
    @Singleton
    public AWSCredentialsProvider provideAWSCredentialsProvider() {
        return DefaultAWSCredentialsProviderChain.getInstance();
    }

    @Provides
    @Nullable
    public TracingHandler provideTracingHandler(boolean enableXRay) {
        return new TracingHandler(AWSXRay.getGlobalRecorder());
    }

    @Provides
    @Singleton
    public AmazonDynamoDB provideAmazonDynamoDB(@Nullable TracingHandler tracingHandler, boolean enableXRay, AWSCredentialsProvider credentialsProvider, Regions regions) {
        AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard()
                .withCredentials(credentialsProvider)
                .withRegion(regions);

        if (enableXRay) {
            builder.withRequestHandlers(tracingHandler);
        }

        return builder.build();
    }

    @Provides
    @Singleton
    public DynamoDBMapper provideDynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Provides
    @Singleton
    public AmazonS3 provideAmazonS3(@Nullable TracingHandler tracingHandler, boolean enableXRay, AWSCredentialsProvider credentialsProvider, Regions regions) {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(regions);

        if (enableXRay) {
            builder.withRequestHandlers(tracingHandler);
        }

        return builder.build();
    }
}
