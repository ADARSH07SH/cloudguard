package com.ash.tools.impl;

import com.ash.tools.Tool;
import com.ash.cloud.CloudProvider;
import com.ash.cloud.CloudProviderFactory;
import com.ash.cloud.Instance;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

public class ListAllCloudInstancesTool implements Tool {

    @Override
    public String getName() {
        return "list_all_instances";
    }

    @Override
    public JsonElement execute(JsonObject arguments) throws Exception {
        
        StringBuilder sb = new StringBuilder();
        sb.append("Instances across all cloud providers:\n\n");

        Map<String, CloudProvider> providers = CloudProviderFactory.getAllProviders();
        
        List<java.util.concurrent.CompletableFuture<String>> futures = providers.values().stream()
                .map(provider -> java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                    StringBuilder providerSb = new StringBuilder();
                    providerSb.append("=== ").append(provider.getProviderName()).append(" ===\n");
                    try {
                        List<Instance> instances = provider.listInstances();
                        if (instances.isEmpty()) {
                            providerSb.append("No instances found\n");
                        } else {
                            for (Instance instance : instances) {
                                providerSb.append("  - ").append(instance.getId())
                                        .append(" (").append(instance.getType()).append(") - ")
                                        .append(instance.getState()).append("\n");
                            }
                        }
                    } catch (Exception e) {
                        providerSb.append("  Error: ").append(e.getMessage()).append("\n");
                    }
                    providerSb.append("\n");
                    return providerSb.toString();
                }))
                .collect(java.util.stream.Collectors.toList());

        java.util.concurrent.CompletableFuture<Void> allOf = java.util.concurrent.CompletableFuture.allOf(futures.toArray(new java.util.concurrent.CompletableFuture[0]));
        allOf.join();

        for (java.util.concurrent.CompletableFuture<String> future : futures) {
            sb.append(future.get());
        }

        JsonObject result = new JsonObject();
        JsonArray content = new JsonArray();
        
        JsonObject textObj = new JsonObject();
        textObj.addProperty("type", "text");
        textObj.addProperty("text", sb.toString());
        
        content.add(textObj);
        result.add("content", content);
        result.addProperty("isError", false);

        return result;
    }
}