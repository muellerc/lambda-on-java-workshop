package com.aws.samples.petclinic.dagger;

import com.aws.samples.petclinic.CreatePetLambdaHandler;
import com.aws.samples.petclinic.pet.MedicalRecordStore;
import com.aws.samples.petclinic.pet.PetRepository;
import com.aws.samples.petclinic.pet.PetService;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = PetModule.class)
public interface PetComponent {

    PetService buildPetService();

    PetRepository buildPetRepository();

    MedicalRecordStore buildMedicalRecordStore();

    void inject(CreatePetLambdaHandler createPetLambdaHandler);
}
