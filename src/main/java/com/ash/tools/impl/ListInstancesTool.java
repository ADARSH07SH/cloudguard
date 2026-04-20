package com.ash.tools.impl;

import com.ash.tools.Tool;
import com.ash.cloud.CloudProvider;
import com.ash.cloud.CloudProviderFactory;
import com.ash.cloud.Instance;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class ListInstancesTool implements Tool {

    @Override
    public String getName() {
        return "list_instances";
    }

    @Override
    public JsonElement execute(JsonObject arguments) throws Exception {
        
        String provider = "aws";
        if (arguments.has("provider") && !arguments.get("provider").isJsonNull()) {
            provider = arguments.get("provider").getAsString();
        }

        CloudProvider cloudProvider = CloudProviderFactory.getProvider(provider);
        List<Instance> instances = cloudProvider.listInstances();

        StringBuilder sb = new StringBuilder();
        sb.append("Instances from ").append(cloudProvider.getProviderName()).append(":\n\n");

        for (Instance instance : instances) {
            sb.append(instance.getId())
                    .append(" (")
                    .append(instance.getType())
                    .append(") - ")
                    .append(instance.getState())
                    .append("\n");
        }

        if (instances.isEmpty()) {
            sb.append("No instances found");
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