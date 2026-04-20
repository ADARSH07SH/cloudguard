package com.ash.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Jwts;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class GcpCloudProvider implements CloudProvider {

    private final HttpClient httpClient;
    private String accessToken;
    private long tokenExpiry;

    public GcpCloudProvider() {
        this.httpClient = HttpClient.newHttpClient();
    }

    private void ensureAuthenticated() throws Exception {
        if (accessToken == null || System.currentTimeMillis() > tokenExpiry) {
            authenticate();
        }
    }

    private void authenticate() throws Exception {
        String serviceAccountJson = CloudConfig.GcpConfig.SERVICE_ACCOUNT_JSON;
        
        if (serviceAccountJson == null) {
            throw new Exception("GCP service account not configured");
        }

        JsonObject serviceAccount = JsonParser.parseString(serviceAccountJson).getAsJsonObject();
        String clientEmail = serviceAccount.get("client_email").getAsString();
        String privateKeyPem = serviceAccount.get("private_key").getAsString();
        String tokenUri = serviceAccount.get("token_uri").getAsString();

        PrivateKey privateKey = parsePrivateKey(privateKeyPem);

        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + 3600000);

        String jwt = Jwts.builder()
                .issuer(clientEmail)
                .audience().add(tokenUri).and()
                .issuedAt(issuedAt)
                .expiration(expiration)
                .claim("scope", CloudConfig.GcpConfig.AUTH_SCOPE)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();

        String body = String.format(
            "grant_type=%s&assertion=%s",
            URLEncoder.encode("urn:ietf:params:oauth:grant-type:jwt-bearer", StandardCharsets.UTF_8),
            URLEncoder.encode(jwt, StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenUri))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject tokenResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            this.accessToken = tokenResponse.get("access_token").getAsString();
            int expiresIn = tokenResponse.get("expires_in").getAsInt();
            this.tokenExpiry = System.currentTimeMillis() + (expiresIn * 1000) - 60000;
        } else {
            throw new Exception("GCP auth failed: " + response.body());
        }
    }

    private PrivateKey parsePrivateKey(String privateKeyPem) throws Exception {
        String privateKeyContent = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    @Override
    public List<Instance> listInstances() throws Exception {
        String projectId = CloudConfig.GcpConfig.PROJECT_ID;
        
        if (projectId == null) {
            List<Instance> mockInstances = new ArrayList<>();
            mockInstances.add(new Instance("gcp-instance-1", "n1-standard-1", "RUNNING", "GCP"));
            mockInstances.add(new Instance("gcp-instance-2", "n1-standard-2", "TERMINATED", "GCP"));
            return mockInstances;
        }

        ensureAuthenticated();

        String url = String.format(
            "%s/projects/%s/aggregated/instances",
            CloudConfig.GcpConfig.COMPUTE_URL,
            projectId
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new Exception("GCP API failed: " + response.body());
        }

        List<Instance> instances = new ArrayList<>();
        JsonObject responseObj = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonObject items = responseObj.getAsJsonObject("items");

        if (items != null) {
            for (String zone : items.keySet()) {
                JsonObject zoneData = items.getAsJsonObject(zone);
                if (zoneData.has("instances")) {
                    JsonArray instancesArray = zoneData.getAsJsonArray("instances");
                    
                    for (int i = 0; i < instancesArray.size(); i++) {
                        JsonObject instance = instancesArray.get(i).getAsJsonObject();
                        String name = instance.get("name").getAsString();
                        String machineType = instance.get("machineType").getAsString();
                        String status = instance.get("status").getAsString();

                        instances.add(new Instance(name, machineType, status, "GCP"));
                    }
                }
            }
        }

        return instances;
    }

    @Override
    public void deleteInstance(String instanceId) throws Exception {
        String projectId = CloudConfig.GcpConfig.PROJECT_ID;
        
        if (projectId == null) {
            System.out.println("GCP: Mock delete of instance " + instanceId);
            return;
        }

        ensureAuthenticated();

        String[] parts = instanceId.split("/");
        String zone = parts.length > 1 ? parts[0] : "us-central1-a";
        String instance = parts.length > 1 ? parts[1] : instanceId;

        String url = String.format(
            "%s/projects/%s/zones/%s/instances/%s",
            CloudConfig.GcpConfig.COMPUTE_URL,
            projectId,
            zone,
            instance
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200 && response.statusCode() != 204) {
            throw new Exception("GCP delete failed: " + response.body());
        }
    }

    @Override
    public String getProviderName() {
        return "GCP";
    }
}