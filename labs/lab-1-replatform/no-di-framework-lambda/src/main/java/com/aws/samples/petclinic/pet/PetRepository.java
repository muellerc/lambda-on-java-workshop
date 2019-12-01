package com.aws.samples.petclinic.pet;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class PetRepository {

    private DynamoDBMapper mapper;

    public PetRepository(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public Pet save(Pet pet) {
        mapper.save(pet);

        return pet;
    }
}