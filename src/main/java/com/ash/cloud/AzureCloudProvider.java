package com.ash.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AzureCloudProvider implements CloudProvider {

    private final HttpClient httpClient;
    private String accessToken;
    private long tokenExpiry;

    public AzureCloudProvider() {
        this.httpClient = HttpClient.newHttpClient();
    }

    private void ensureAuthenticated() throws Exception {
        if (accessToken == null || System.currentTimeMillis() > tokenExpiry) {
            authenticate();
        }
    }

    private void authenticate() throws Exception {
        String tenantId = CloudConfig.AzureConfig.TENANT_ID;
        String clientId = CloudConfig.AzureConfig.CLIENT_ID;
        String clientSecret = CloudConfig.AzureConfig.CLIENT_SECRET;

        if (tenantId == null || clientId == null || clientSecret == null) {
            throw new Exception("Azure credentials not configured");
        }

        String tokenUrl = String.format(CloudConfig.AzureConfig.TOKEN_URL, tenantId);
        
        String body = String.format(
            "grant_type=client_credentials&client_id=%s&client_secret=%s&scope=%s",
            URLEncoder.encode(clientId, StandardCharsets.UTF_8),
            URLEncoder.encode(clientSecret, StandardCharsets.UTF_8),
            URLEncoder.encode("https://management.azure.com/.default", StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Azure auth failed: " + response.body());
        }

        JsonObject tokenResponse = JsonParser.parseString(response.body()).getAsJsonObject();
        this.accessToken = tokenResponse.get("access_token").getAsString();
        int expiresIn = tokenResponse.get("expires_in").getAsInt();
        this.tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000) - 60000;
    }

    @Override
    public List<Instance> listInstances() throws Exception {
        ensureAuthenticated();
        
        String subscriptionId = CloudConfig.AzureConfig.SUBSCRIPTION_ID;
        if (subscriptionId == null) {
            throw new Exception("Azure subscription ID not configured");
        }

        String url = String.format(
            "%s/subscriptions/%s/providers/Microsoft.Compute/virtualMachines?api-version=%s",
            CloudConfig.AzureConfig.MANAGEMENT_URL,
            subscriptionId,
            CloudConfig.AzureConfig.API_VERSION
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("Azure API failed: " + response.body());
        }

        List<Instance> instances = new ArrayList<>();
        JsonObject responseObj = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray vms = responseObj.getAsJsonArray("value");

        for (int i = 0; i < vms.size(); i++) {
            JsonObject vm = vms.get(i).getAsJsonObject();
            String id = vm.get("id").getAsString();
            String name = vm.get("name").getAsString();
            
            JsonObject properties = vm.getAsJsonObject("properties");
            String vmSize = properties.getAsJsonObject("hardwareProfile").get("vmSize").getAsString();
            String provisioningState = properties.get("provisioningState").getAsString();

            instances.add(new Instance(name, vmSize, provisioningState, "Azure"));
        }

        return instances;
    }

    @Override
    public void deleteInstance(String instanceId) throws Exception {
        ensureAuthenticated();
        
        String subscriptionId = CloudConfig.AzureConfig.SUBSCRIPTION_ID;
        if (subscriptionId == null) {
            throw new Exception("Azure subscription ID not configured");
        }

        String url = String.format(
            "%s%s?api-version=%s",
            CloudConfig.AzureConfig.MANAGEMENT_URL,
            instanceId,
            CloudConfig.AzureConfig.API_VERSION
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 202 && response.statusCode() != 204) {
            throw new Exception("Azure delete failed: " + response.body());
        }
    }

    @Override
    public String getProviderName() {
        return "Azure";
    }
}