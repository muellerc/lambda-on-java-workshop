package com.aws.samples.petclinic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRuntime {

    private static final String LAMBDA_VERSION_DATE = "2018-06-01";
    private static final String LAMBDA_RUNTIME_URL_TEMPLATE = "http://{0}/{1}/runtime/invocation/next";
    private static final String LAMBDA_INVOCATION_URL_TEMPLATE = "http://{0}/{1}/runtime/invocation/{2}/response";
    private static final String LAMBDA_INIT_ERROR_URL_TEMPLATE = "http://{0}/{1}/runtime/init/error";
    private static final String LAMBDA_ERROR_URL_TEMPLATE = "http://{0}/{1}/runtime/invocation/{2}/error";
    private static final String ERROR_RESPONSE_TEMPLATE = "{\"errorMessage\": \"{0}\",\"errorType\": \"{1}\"}";

    public static void main(String args[]) {
        System.out.println("starting the custom runtime...");

        String runtimeApi = getEnv("AWS_LAMBDA_RUNTIME_API");
        String taskRoot = getEnv("LAMBDA_TASK_ROOT");

        URL runtimeUrl = null;
        CreatePetLambdaStreamHandler handler = null;

        try {
            System.out.println("rcreating the handler...");
            handler = new CreatePetLambdaStreamHandler();

            System.out.println("creating the URL...");
            runtimeUrl = new URL(MessageFormat.format(LAMBDA_RUNTIME_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE));
        } catch (Exception e) {
            String initErrorUrl = MessageFormat.format(LAMBDA_INIT_ERROR_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE);
            postError(initErrorUrl, "Could not find handler method", "InitError");
            e.printStackTrace();
            return;
        }

        // Main event loop
        while (true) {
            String requestId = null;

            try{
                // Get next Lambda Event
                System.out.println("requesting next event...");
                SimpleHttpResponse event = get(runtimeUrl);
                requestId = event.getHeader("Lambda-Runtime-Aws-Request-Id");
                System.out.println("got event with id: " + requestId);

                // Invoke Handler Method
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                System.out.println("calling the handler...");
                handler.handleRequest(new ByteArrayInputStream(event.getBody().getBytes("UTF-8")), bos, null);

                // Post the results of Handler Invocation
                System.out.println("posting the result...");
                String invocationUrl = MessageFormat.format(LAMBDA_INVOCATION_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE, requestId);
                post(invocationUrl, bos.toString("UTF-8"));
                System.out.println("done");
            } catch (Exception e) {
                String initErrorUrl = MessageFormat.format(LAMBDA_ERROR_URL_TEMPLATE, runtimeApi, LAMBDA_VERSION_DATE, requestId);
                postError(initErrorUrl, "Invocation Error", "RuntimeError");
                e.printStackTrace();
            }
        }
    }

    private static void postError(String errorUrl, String errMsg, String errType) {
        String error = MessageFormat.format(ERROR_RESPONSE_TEMPLATE, errMsg, errType);
        try {
            post(errorUrl, error);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getEnv(String name) {
        return System.getenv(name);
    }

    private static SimpleHttpResponse get(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Parse the HTTP Response
        SimpleHttpResponse output = readResponse(conn);

        return output;
    }

    private static SimpleHttpResponse post(String remoteUrl, String body) throws IOException {
        URL url = new URL(remoteUrl);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        setBody(conn, body);
        conn.connect();

        // We can probably skip this for speed because we don't really care about the response
        SimpleHttpResponse output = readResponse(conn);

        return output;
    }

    private static SimpleHttpResponse readResponse(HttpURLConnection conn) throws IOException{
        // Map Response Headers
        HashMap<String, List<String>> headers = new HashMap<>();

        for(Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            headers.put(entry.getKey(), entry.getValue());
        }

        // Map Response Body
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();

        String line;

        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        rd.close();

        return new SimpleHttpResponse(conn.getResponseCode(), headers, result.toString());
    }

    private static void setBody(HttpURLConnection conn, String body) throws IOException {
        OutputStream os = null;
        OutputStreamWriter osw = null;

        try {
            os = conn.getOutputStream();
            osw = new OutputStreamWriter(os, "UTF-8");

            osw.write(body);
            osw.flush();
        } finally {
            osw.close();
            os.close();
        }
    }
}
