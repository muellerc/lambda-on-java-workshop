package com.aws.samples.petclinic.pet;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class PetRepository {

    private DynamoDbClient dynamoDbClient;

    public PetRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public Pet save(Pet pet) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName("Pets")
                .item(asItem(pet))
                .build());

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
}

