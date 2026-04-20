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
        
        for (Map.Entry<String, CloudProvider> entry : providers.entrySet()) {
            CloudProvider provider = entry.getValue();
            sb.append("=== ").append(provider.getProviderName()).append(" ===\n");
            
            try {
                List<Instance> instances = provider.listInstances();
                
                if (instances.isEmpty()) {
                    sb.append("No instances found\n");
                } else {
                    for (Instance instance : instances) {
                        sb.append("  - ").append(instance.getId())
                                .append(" (").append(instance.getType()).append(") - ")
                                .append(instance.getState()).append("\n");
                    }
                }
            } catch (Exception e) {
                sb.append("  Error: ").append(e.getMessage()).append("\n");
            }
            
            sb.append("\n");
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