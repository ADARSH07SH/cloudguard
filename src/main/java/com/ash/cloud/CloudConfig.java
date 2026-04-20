package com.ash.cloud;

public class CloudConfig {
    
    public static class AzureConfig {
        public static final String TENANT_ID = System.getenv("AZURE_TENANT_ID");
        public static final String CLIENT_ID = System.getenv("AZURE_CLIENT_ID");
        public static final String CLIENT_SECRET = System.getenv("AZURE_CLIENT_SECRET");
        public static final String SUBSCRIPTION_ID = System.getenv("AZURE_SUBSCRIPTION_ID");
        public static final String TOKEN_URL = "https://login.microsoftonline.com/%s/oauth2/v2.0/token";
        public static final String MANAGEMENT_URL = "https://management.azure.com";
        public static final String API_VERSION = "2023-09-01";
    }
    
    public static class GcpConfig {
        public static final String PROJECT_ID = System.getenv("GCP_PROJECT_ID");
        public static final String SERVICE_ACCOUNT_JSON = System.getenv("GCP_SERVICE_ACCOUNT_JSON");
        public static final String COMPUTE_URL = "https://compute.googleapis.com/compute/v1";
        public static final String AUTH_SCOPE = "https://www.googleapis.com/auth/cloud-platform";
    }
    
    public static class AwsConfig {
        public static final String REGION = System.getenv().getOrDefault("AWS_REGION", "ap-south-1");
    }
}