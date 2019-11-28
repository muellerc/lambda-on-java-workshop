package cmr;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Main {

    private static String PET_RECORD = "{\"name\": \"Max\", \"type\": \"dog\", \"birthday\": \"2010-11-03\", \"medicalRecord\": \"bla bla bla\"}";

    private static String API_GATEWAY_EVENT= "{\n" +
            "  \"body\": \"{\\\"name\\\": \\\"Max\\\", \\\"type\\\": \\\"dog\\\", \\\"birthday\\\": \\\"2010-11-03\\\", \\\"medicalRecord\\\": \\\"bla bla bla\\\"}\",\n" +
            "  \"resource\": \"/{proxy+}\",\n" +
            "  \"path\": \"/path/to/resource\",\n" +
            "  \"httpMethod\": \"POST\",\n" +
            "  \"isBase64Encoded\": true,\n" +
            "  \"queryStringParameters\": {\n" +
            "    \"foo\": \"bar\"\n" +
            "  },\n" +
            "  \"multiValueQueryStringParameters\": {\n" +
            "    \"foo\": [\n" +
            "      \"bar\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"pathParameters\": {\n" +
            "    \"proxy\": \"/path/to/resource\"\n" +
            "  },\n" +
            "  \"stageVariables\": {\n" +
            "    \"baz\": \"qux\"\n" +
            "  },\n" +
            "  \"headers\": {\n" +
            "    \"Accept\": \"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\",\n" +
            "    \"Accept-Encoding\": \"gzip, deflate, sdch\",\n" +
            "    \"Accept-Language\": \"en-US,en;q=0.8\",\n" +
            "    \"Cache-Control\": \"max-age=0\",\n" +
            "    \"CloudFront-Forwarded-Proto\": \"https\",\n" +
            "    \"CloudFront-Is-Desktop-Viewer\": \"true\",\n" +
            "    \"CloudFront-Is-Mobile-Viewer\": \"false\",\n" +
            "    \"CloudFront-Is-SmartTV-Viewer\": \"false\",\n" +
            "    \"CloudFront-Is-Tablet-Viewer\": \"false\",\n" +
            "    \"CloudFront-Viewer-Country\": \"US\",\n" +
            "    \"Host\": \"1234567890.execute-api.us-east-1.amazonaws.com\",\n" +
            "    \"Upgrade-Insecure-Requests\": \"1\",\n" +
            "    \"User-Agent\": \"Custom User Agent String\",\n" +
            "    \"Via\": \"1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)\",\n" +
            "    \"X-Amz-Cf-Id\": \"cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA==\",\n" +
            "    \"X-Forwarded-For\": \"127.0.0.1, 127.0.0.2\",\n" +
            "    \"X-Forwarded-Port\": \"443\",\n" +
            "    \"X-Forwarded-Proto\": \"https\"\n" +
            "  },\n" +
            "  \"multiValueHeaders\": {\n" +
            "    \"Accept\": [\n" +
            "      \"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\"\n" +
            "    ],\n" +
            "    \"Accept-Encoding\": [\n" +
            "      \"gzip, deflate, sdch\"\n" +
            "    ],\n" +
            "    \"Accept-Language\": [\n" +
            "      \"en-US,en;q=0.8\"\n" +
            "    ],\n" +
            "    \"Cache-Control\": [\n" +
            "      \"max-age=0\"\n" +
            "    ],\n" +
            "    \"CloudFront-Forwarded-Proto\": [\n" +
            "      \"https\"\n" +
            "    ],\n" +
            "    \"CloudFront-Is-Desktop-Viewer\": [\n" +
            "      \"true\"\n" +
            "    ],\n" +
            "    \"CloudFront-Is-Mobile-Viewer\": [\n" +
            "      \"false\"\n" +
            "    ],\n" +
            "    \"CloudFront-Is-SmartTV-Viewer\": [\n" +
            "      \"false\"\n" +
            "    ],\n" +
            "    \"CloudFront-Is-Tablet-Viewer\": [\n" +
            "      \"false\"\n" +
            "    ],\n" +
            "    \"CloudFront-Viewer-Country\": [\n" +
            "      \"US\"\n" +
            "    ],\n" +
            "    \"Host\": [\n" +
            "      \"0123456789.execute-api.us-east-1.amazonaws.com\"\n" +
            "    ],\n" +
            "    \"Upgrade-Insecure-Requests\": [\n" +
            "      \"1\"\n" +
            "    ],\n" +
            "    \"User-Agent\": [\n" +
            "      \"Custom User Agent String\"\n" +
            "    ],\n" +
            "    \"Via\": [\n" +
            "      \"1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)\"\n" +
            "    ],\n" +
            "    \"X-Amz-Cf-Id\": [\n" +
            "      \"cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA==\"\n" +
            "    ],\n" +
            "    \"X-Forwarded-For\": [\n" +
            "      \"127.0.0.1, 127.0.0.2\"\n" +
            "    ],\n" +
            "    \"X-Forwarded-Port\": [\n" +
            "      \"443\"\n" +
            "    ],\n" +
            "    \"X-Forwarded-Proto\": [\n" +
            "      \"https\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"requestContext\": {\n" +
            "    \"accountId\": \"123456789012\",\n" +
            "    \"resourceId\": \"123456\",\n" +
            "    \"stage\": \"prod\",\n" +
            "    \"requestId\": \"c6af9ac6-7b61-11e6-9a41-93e8deadbeef\",\n" +
            "    \"requestTime\": \"09/Apr/2015:12:34:56 +0000\",\n" +
            "    \"requestTimeEpoch\": 1428582896000,\n" +
            "    \"identity\": {\n" +
            "      \"cognitoIdentityPoolId\": null,\n" +
            "      \"accountId\": null,\n" +
            "      \"cognitoIdentityId\": null,\n" +
            "      \"caller\": null,\n" +
            "      \"accessKey\": null,\n" +
            "      \"sourceIp\": \"127.0.0.1\",\n" +
            "      \"cognitoAuthenticationType\": null,\n" +
            "      \"cognitoAuthenticationProvider\": null,\n" +
            "      \"userArn\": null,\n" +
            "      \"userAgent\": \"Custom User Agent String\",\n" +
            "      \"user\": null\n" +
            "    },\n" +
            "    \"path\": \"/prod/path/to/resource\",\n" +
            "    \"resourcePath\": \"/{proxy+}\",\n" +
            "    \"httpMethod\": \"POST\",\n" +
            "    \"apiId\": \"1234567890\",\n" +
            "    \"protocol\": \"HTTP/1.1\"\n" +
            "  }\n" +
            "}";

    public static void main(String[] args) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream(API_GATEWAY_EVENT.getBytes("UTF-8"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new CreatePetLambdaStreamHandler().handleRequest(input, output, null);
        System.out.println(output.toString("UTF-8"));

//        APIGatewayV2ProxyRequestEvent event = new APIGatewayV2ProxyRequestEvent();
//        event.setBody(PET_RECORD);
//        System.out.println(new CreatePetLambdaHandler().handleRequest(event, null));
    }
}