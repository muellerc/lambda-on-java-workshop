package com.aws.samples.petclinic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.aws.samples.petclinic.pet.Pet;
import com.aws.samples.petclinic.pet.PetService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Named("StreamCreatePet")
public class LambdaStreamHandlerCreatePet implements RequestStreamHandler {

    @Inject
    PetService service;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
//        Subsegment injectorSegment = AWSXRay.beginSubsegment("handleRequest");

        try {
            JsonNode inputNode = objectMapper.readTree(inputStream);
            JsonNode bodyNode = objectMapper.readTree(inputNode.get("body").asText());

            Pet pet = service.addPet(fromJsonNode(bodyNode));

            ObjectNode outputNode = objectMapper.createObjectNode();
            outputNode.put("statusCode", 200);
            outputNode.put("body", objectMapper.writeValueAsString(asJsonNode(pet)));

            objectMapper.writeValue(outputStream, outputNode);
        } catch (Exception e) {
//            injectorSegment.addException(e);
            e.printStackTrace();
        } finally {
//            AWSXRay.endSubsegment();
        }
    }

    private Object asJsonNode(Pet pet) {
        ObjectNode petNode = objectMapper.createObjectNode();
        petNode.put("id", pet.getId());
        petNode.put("name", pet.getName());
        petNode.put("type", pet.getType());
        petNode.put("birthday", pet.getBirthday());

        return petNode;
    }

    private Pet fromJsonNode(JsonNode bodyNode) {
        Pet pet = new Pet();
        pet.setName(bodyNode.get("name").asText());
        pet.setType(bodyNode.get("type").asText());
        pet.setBirthday(bodyNode.get("birthday").asText());

        return pet;
    }
}