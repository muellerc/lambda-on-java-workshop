package com.aws.samples.petclinic.pet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PetService {

    @Inject
    PetRepository petRepository;

    @Inject
    MedicalRecordStore medicalRecordStore;

    public PetRecord addPet(PetRecord petRecord) {
        petRecord.setId(UUID.randomUUID().toString());

        petRepository.save(new Pet(petRecord.getId(), petRecord.getName(), petRecord.getType(), petRecord.getBirthday()));
        medicalRecordStore.save(new MedicalRecord(petRecord.getId(), petRecord.getMedicalRecord()));

        return petRecord;
    }
}
