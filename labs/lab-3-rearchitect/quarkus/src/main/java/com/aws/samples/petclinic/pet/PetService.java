package com.aws.samples.petclinic.pet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
public class PetService {

    @Inject
    PetRepository petRepository;

    public Iterable<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Optional<Pet> getPet(String id) {
        return petRepository.findById(id);
    }

    public Pet addPet(Pet pet) {
        return petRepository.save(pet);
    }

    public void deletePet(Pet pet) {
        petRepository.delete(pet);
    }
}
