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

        Future<Pet> petFuture = executorService.submit(new Callable<Pet>() {
            public Pet call() throws Exception {
                return petRepository.save(new Pet(petRecord.getId(), petRecord.getName(), petRecord.getType(), petRecord.getBirthday()));
            }
        });
        Future<MedicalRecord> medicalRecordFuture = executorService.submit(new Callable<MedicalRecord>() {
            public MedicalRecord call() throws Exception {
                return medicalRecordStore.save(new MedicalRecord(petRecord.getId(), petRecord.getMedicalRecord()));
            }
        });

        petFuture.get();
        medicalRecordFuture.get();

        return petRecord;
    }
}
