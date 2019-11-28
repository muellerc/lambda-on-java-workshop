package com.aws.samples.petclinic.pet;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

@RestController
class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "ok";
    }

    @PostMapping("/pet")
    public PetRecord addPet(@RequestBody PetRecord petRecord) {
        return petService.addPet(petRecord);
    }
}