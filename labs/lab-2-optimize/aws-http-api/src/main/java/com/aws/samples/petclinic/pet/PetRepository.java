package com.aws.samples.petclinic.pet;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.*;

public class PetRepository {

    private DynamoDbClient dynamoDbClient;
    private String table;

    public PetRepository(DynamoDbClient dynamoDbClient, String table) {
        this.dynamoDbClient = dynamoDbClient;
        this.table = table;
    }

    public Pet save(Pet pet) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(table)
                .item(asItem(pet))
                .build());

        return pet;
    }

    private Map<String, AttributeValue> asItem(Pet pet) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(pet.getId()).build());
        item.put("name", AttributeValue.builder().s(pet.getName()).build());
        item.put("type", AttributeValue.builder().s(pet.getType()).build());
        item.put("birthday", AttributeValue.builder().s(pet.getBirthday()).build());
        return item;
    }
}

