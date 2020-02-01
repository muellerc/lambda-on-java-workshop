package com.aws.samples.petclinic.pet;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PetService {

    private ExecutorService executorService;
    private PetRepository petRepository;
    private MedicalRecordStore medicalRecordStore;

    public PetService(ExecutorService executorService, PetRepository petRepository, MedicalRecordStore medicalRecordStore) {
        this.executorService = executorService;
        this.petRepository = petRepository;
        this.medicalRecordStore = medicalRecordStore;
    }

    public PetRecord addPet(PetRecord petRecord) throws ExecutionException, InterruptedException {
        petRecord.setId(UUID.randomUUID().toString());

        petRepository.save(new Pet(petRecord.getId(), petRecord.getName(), petRecord.getType(), petRecord.getBirthday()));
        medicalRecordStore.save(new MedicalRecord(petRecord.getId(), petRecord.getMedicalRecord()));

        return petRecord;
    }
}
