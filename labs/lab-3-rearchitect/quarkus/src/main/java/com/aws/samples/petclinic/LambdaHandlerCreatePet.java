package com.aws.samples.petclinic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.aws.samples.petclinic.pet.Pet;
import com.aws.samples.petclinic.pet.PetRecord;
import com.aws.samples.petclinic.pet.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Named;

@Named("CreatePet")
public class LambdaHandlerCreatePet implements RequestHandler<APIGatewayV2ProxyRequestEvent, APIGatewayV2ProxyResponseEvent> {

    @Inject
    PetService service;

    @Inject
    ObjectMapper objectMapper;

    @Override
    public APIGatewayV2ProxyResponseEvent handleRequest(APIGatewayV2ProxyRequestEvent input, Context context) {
//        Subsegment injectorSegment = AWSXRay.beginSubsegment("handleRequest");

        APIGatewayV2ProxyResponseEvent response = new APIGatewayV2ProxyResponseEvent();
        response.setStatusCode(200);

        try {
            PetRecord pet = objectMapper.readValue(input.getBody(), PetRecord.class);
            pet = service.addPet(pet);
            response.setBody(objectMapper.writeValueAsString(pet));
        } catch (Exception e) {
            response.setStatusCode(500);
            e.printStackTrace();
//            injectorSegment.addException(e);
        } finally {
//            AWSXRay.endSubsegment();
        }

        return response;
    }
}
