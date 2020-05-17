package com.aws.samples.petclinic.cdi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.net.URI;
import java.net.URISyntaxException;

import io.quarkus.arc.DefaultBean;
import software.amazon.awssdk.services.s3.S3Client;

@Dependent
public class ClientBuilderConfiguration {

    @Produces
    public Region buildRegion() {
        return Region.of(System.getenv("AWS_REGION"));
    }

    @Produces
    public AwsCredentialsProvider buildAwsCredentialsProvider() {
        return DefaultCredentialsProvider.create();
    }

    @Produces
    public SdkHttpClient buildSdkHttpClient() {
        return UrlConnectionHttpClient.builder().build();
    }

    @Produces @DefaultBean
    public DynamoDbClient buildDynamoDbClient(Region region, AwsCredentialsProvider credentialsProvider, SdkHttpClient sdkHttpClient) throws URISyntaxException {
        return DynamoDbClient.builder()
                .httpClient(sdkHttpClient)
                .credentialsProvider(credentialsProvider)
                .region(region)
                .endpointOverride(new URI(String.format("https://dynamodb.%s.amazonaws.com", region.id())))
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }

    @Produces @DefaultBean
    public S3Client buildS3Client(Region region, AwsCredentialsProvider credentialsProvider, SdkHttpClient sdkHttpClient) throws URISyntaxException {
        return S3Client.builder()
                .httpClient(sdkHttpClient)
                .credentialsProvider(credentialsProvider)
                .region(region)
                .endpointOverride(new URI(String.format("https://s3.%s.amazonaws.com", region.id())))
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();
    }

    @Produces
    public ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }
}
