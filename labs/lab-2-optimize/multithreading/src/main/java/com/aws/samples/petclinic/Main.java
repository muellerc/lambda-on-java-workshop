package com.aws.samples.petclinic;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;

public class Main {

    /*
    only for test purpose
    */
    public static void main(String[] args) {
        CreatePetLambdaHandler handler = new CreatePetLambdaHandler();

        APIGatewayV2ProxyRequestEvent request = new APIGatewayV2ProxyRequestEvent();
        request.setBody("{\"name\": \"Max\", \"type\": \"dog\", \"birthday\": \"2010-11-03\", \"medicalRecord\": \"bla bla bla\"}");
        Context context = null;

        APIGatewayV2ProxyResponseEvent response = handler.handleRequest(request, context);
        handler.executorService.shutdownNow();

        System.out.println("response:" + response);
    }
}
