package com.fk.platform.keycloak.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Example client application using API Key authentication with Keycloak.
 */
public class APIKeyClientExample {

    private static final String API_URL = "https://your-api-server/protected-resource";
    private static final String API_KEY = "your-api-key";

    public static void main(String[] args) {
        try {
            // Create HTTP client
            HttpClient httpClient = HttpClients.createDefault();

            // Create request with API Key header
            HttpGet request = new HttpGet(API_URL);
            request.addHeader("X-API-Key", API_KEY);

            // Execute request
            System.out.println("Sending request to: " + API_URL);
            HttpResponse response = httpClient.execute(request);

            // Process response
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            System.out.println("Response Status: " + statusCode);

            if (statusCode == 200) {
                System.out.println("Authentication successful!");
                System.out.println("Response: " + responseBody);
            } else if (statusCode == 401) {
                System.out.println("Authentication failed. Invalid API key.");
            } else {
                System.out.println("Request failed: " + response.getStatusLine().getReasonPhrase());
            }

        } catch (IOException e) {
            System.err.println("Error making API request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Example method to generate or refresh an API key.
     *
     * @param accessToken A valid Keycloak access token
     * @return The new API key
     */
    public static String generateApiKey(String accessToken) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://your-keycloak-server/auth/realms/your-realm/api-keys/generate");
        request.addHeader("Authorization", "Bearer " + accessToken);

        HttpResponse response = httpClient.execute(request);

        if (response.getStatusLine().getStatusCode() == 200) {
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject json = new JSONObject(responseBody);
            return json.getString("apiKey");
        } else {
            throw new IOException("Failed to generate API key: " + response.getStatusLine().getReasonPhrase());
        }
    }
}