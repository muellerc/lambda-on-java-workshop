package com.aws.samples.petclinic.pet;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PetRepository {

    private DynamoDBMapper mapper;

    @Inject
    public PetRepository(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public Pet save(Pet pet) {
        mapper.save(pet);

        return pet;
    }
}