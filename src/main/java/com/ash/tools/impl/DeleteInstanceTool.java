package com.ash.tools.impl;

import com.ash.tools.Tool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DeleteInstanceTool implements Tool {

    @Override
    public JsonElement execute(JsonObject arguments) throws Exception {

        if (!arguments.has("id") || arguments.get("id").isJsonNull()) {
            throw new Exception("Missing required parameter: id");
        }

        String instanceId = arguments.get("id").getAsString();

        JsonObject result = new JsonObject();
        JsonArray content = new JsonArray();

        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", "Instance " + instanceId + " deletion simulated (dummy implementation)");

        content.add(textContent);
        result.add("content", content);
        result.addProperty("isError", false);

        return result;
    }
}