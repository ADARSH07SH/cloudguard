package com.ash.cloud;

import java.util.HashMap;
import java.util.Map;

public class CloudProviderFactory {
    
    private static final Map<String, CloudProvider> providers = new HashMap<>();
    
    static {
        providers.put("aws", new AwsCloudProvider());
        providers.put("gcp", new GcpCloudProvider());
        providers.put("azure", new AzureCloudProvider());
    }
    
    public static CloudProvider getProvider(String providerName) {
        CloudProvider provider = providers.get(providerName.toLowerCase());
        if (provider == null) {
            throw new IllegalArgumentException("Unknown cloud provider: " + providerName);
        }
        return provider;
    }
    
    public static Map<String, CloudProvider> getAllProviders() {
        return new HashMap<>(providers);
    }
}