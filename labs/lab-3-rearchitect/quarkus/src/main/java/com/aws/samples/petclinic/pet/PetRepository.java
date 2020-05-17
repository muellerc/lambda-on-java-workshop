package com.aws.samples.petclinic.pet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@ApplicationScoped
public class PetRepository {

    @Inject
    DynamoDbClient dynamoDbClient;

    public Iterable<Pet> findAll() {
        ScanResponse response = dynamoDbClient.scan(ScanRequest.builder().tableName("Pets").build());

        List<Pet> pets = new ArrayList<>();
        for (Map<String, AttributeValue> item : response.items()) {
            pets.add(fromItem(item));
        }

        return pets;
    }

    public Optional<Pet> findById(String id) {
        return null;
    }

    public Pet save(Pet pet) {
        pet.setId(UUID.randomUUID().toString());

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName("Pets")
                .item(asItem(pet))
                .build());

        return pet;
    }

    public void delete(Pet pet) {

    }

    private Pet fromItem(Map<String, AttributeValue> item) {
        Pet pet = new Pet();
        pet.setId(item.get("id").s());
        pet.setName(item.get("name").s());
        pet.setType(item.get("type").s());
        pet.setBirthday(item.get("birthday").s());

        return pet;
    }

    private Map<String, AttributeValue> asItem(Pet pet) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(pet.getId()).build());
        item.put("name", AttributeValue.builder().s(pet.getName()).build());
        item.put("tyoe", AttributeValue.builder().s(pet.getType()).build());
        item.put("birthday", AttributeValue.builder().s(pet.getBirthday()).build());
        return item;
    }

    public static void main(String[] args) throws URISyntaxException, JsonProcessingException {
        DynamoDbClient client = DynamoDbClient.builder()
                .httpClient(UrlConnectionHttpClient.builder().build())
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.EU_CENTRAL_1)
                .endpointOverride(new URI(String.format("https://dynamodb.%s.amazonaws.com", Region.EU_CENTRAL_1.id())))
                .overrideConfiguration(ClientOverrideConfiguration.builder().build())
                .build();

        PetRepository repo = new PetRepository();
        repo.dynamoDbClient = client;
        Iterable<Pet> pets = repo.findAll();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        System.out.println(mapper.writeValueAsString(pets));
    }
}
