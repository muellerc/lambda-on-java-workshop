package com.aws.samples.petclinic.pet;

import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.springframework.stereotype.Service;

@Service
class PetService {

    private AmazonDynamoDB amazonDynamoDB;
    private PetRepository petRepository;
    private MedicalRecordStore medicalRecordStore;

    public PetService(AmazonDynamoDB amazonDynamoDB, PetRepository petRepository, MedicalRecordStore medicalRecordStore) {
        this.amazonDynamoDB = amazonDynamoDB;
        this.petRepository = petRepository;
        this.medicalRecordStore = medicalRecordStore;
    }

    public PetRecord addPet(PetRecord petRecord) {
        petRecord.setId(UUID.randomUUID().toString());

        petRepository.save(new Pet(petRecord.getId(), petRecord.getName(), petRecord.getType(), petRecord.getBirthday()));
        medicalRecordStore.save(new MedicalRecord(petRecord.getId(), petRecord.getMedicalRecord()));

        return petRecord;
    }
}