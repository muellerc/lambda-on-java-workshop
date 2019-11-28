package cmr;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreatePetLambdaStreamHandler implements RequestStreamHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DynamoDbClient dynamoDbClient;
    private S3Client s3Client;

    public CreatePetLambdaStreamHandler() {
        try {
            Region region = Region.of(System.getenv("AWS_REGION"));

            AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();

            SdkHttpClient dynamoDbDdkHttpClient = UrlConnectionHttpClient.builder().build();

            dynamoDbClient = DynamoDbClient.builder()
                    .httpClient(dynamoDbDdkHttpClient)
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .endpointOverride(new URI(String.format("https://dynamodb.%s.amazonaws.com", region.id())))
                    .overrideConfiguration(
                            ClientOverrideConfiguration
                                    .builder()
//                                .addExecutionInterceptor(tracingInterceptor)
                                    .build())
                    .build();


            SdkHttpClient s3sdkHttpClient = UrlConnectionHttpClient.builder().build();

            s3Client = S3Client.builder()
                    .httpClient(s3sdkHttpClient)
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .endpointOverride(new URI(String.format("https://s3.%s.amazonaws.com", region.id())))
                    .overrideConfiguration(
                            ClientOverrideConfiguration
                                    .builder()
//                                    .addExecutionInterceptor(tracingInterceptor)
                                    .build())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        try {
            //TODO: Fix APIGatewayV2ProxyRequestEvent$RequestContext["path"] -> String ResourcePath
            //APIGatewayV2ProxyRequestEvent event = objectMapper.readValue(inputStream, APIGatewayV2ProxyRequestEvent.class);

            JsonNode inputNode = objectMapper.readTree(inputStream);
            System.out.println("Received event: " + objectMapper.writeValueAsString(inputNode));

            JsonNode bodyNode = objectMapper.readTree(inputNode.get("body").asText());

            PetRecord petRecord = fromJsonNode(bodyNode);
            petRecord.setId(UUID.randomUUID().toString());

            System.out.println("put item into DynamoDB...");
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName("Pets")
                    .item(asItem(petRecord))
                    .build());

            System.out.println("put object into S3...");
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket("cmr-lambda")
                            .key("medical-record/" + petRecord.getId())
                            .build(),
                    RequestBody.fromString(petRecord.getMedicalRecord()));

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("statusCode", 200);
            outputNode.put("body", objectMapper.writeValueAsString(asJsonNode(petRecord)));

            System.out.println("Returning response: " + objectMapper.writeValueAsString(outputNode));
            objectMapper.writeValue(outputStream, outputNode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private ObjectNode asJsonNode(PetRecord pet) {
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", pet.getId());
        petNode.put("name", pet.getName());
        petNode.put("type", pet.getType());
        petNode.put("birthday", pet.getBirthday());

        return petNode;
    }

    private PetRecord fromJsonNode(JsonNode bodyNode) {
        PetRecord pet = new PetRecord();
        pet.setName(bodyNode.get("name").asText());
        pet.setType(bodyNode.get("type").asText());
        pet.setBirthday(bodyNode.get("birthday").asText());
        pet.setMedicalRecord(bodyNode.get("medicalRecord").asText());

        return pet;
    }

    private Map<String, AttributeValue> asItem(PetRecord pet) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(pet.getId()).build());
        item.put("name", AttributeValue.builder().s(pet.getName()).build());
        item.put("tyoe", AttributeValue.builder().s(pet.getType()).build());
        item.put("birthday", AttributeValue.builder().s(pet.getBirthday()).build());
        return item;
    }
}