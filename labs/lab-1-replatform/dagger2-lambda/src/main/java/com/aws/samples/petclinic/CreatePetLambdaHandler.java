package com.aws.samples.petclinic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import com.aws.samples.petclinic.dagger.DaggerPetComponent;
import com.aws.samples.petclinic.dagger.PetComponent;
import com.aws.samples.petclinic.pet.Pet;
import com.aws.samples.petclinic.pet.PetRecord;
import com.aws.samples.petclinic.pet.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Named;

@Named("CreatePet")
public class CreatePetLambdaHandler implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    @Inject
    PetService service;

    @Inject
    ObjectMapper objectMapper;

    private PetComponent component;

    public CreatePetLambdaHandler() {
        component = DaggerPetComponent.builder().build();
        component.inject(this);
    }

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
        Subsegment injectorSegment = AWSXRay.beginSubsegment("handleRequest");

        APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(200);

        try {
            PetRecord petRecord = objectMapper.readValue(input.getBody(), PetRecord.class);
            petRecord = service.addPet(petRecord);
            response.setBody(objectMapper.writeValueAsString(petRecord));
        } catch (Exception e) {
            response.setStatusCode(500);
            e.printStackTrace();
            injectorSegment.addException(e);
        } finally {
            AWSXRay.endSubsegment();
        }

        return response;
    }
}