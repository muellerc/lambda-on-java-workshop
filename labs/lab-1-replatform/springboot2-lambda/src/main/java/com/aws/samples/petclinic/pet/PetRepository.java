package com.aws.samples.petclinic.pet;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface PetRepository extends CrudRepository<Pet, String> {

}

