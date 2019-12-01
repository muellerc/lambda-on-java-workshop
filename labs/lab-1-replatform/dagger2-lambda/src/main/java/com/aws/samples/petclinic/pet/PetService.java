package com.aws.samples.petclinic.pet;

import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PetService {

    private PetRepository petRepository;
    private MedicalRecordStore medicalRecordStore;

    @Inject
    public PetService(PetRepository petRepository, MedicalRecordStore medicalRecordStore) {
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