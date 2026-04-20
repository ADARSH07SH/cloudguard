package com.ash.tools.impl;

import com.ash.tools.Tool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class EchoTool implements Tool {
    
    @Override
    public String getName() {
        return "echo_tool";
    }
    
    @Override
    public JsonElement execute(JsonObject arguments) throws Exception {
        if (!arguments.has("message") || arguments.get("message").isJsonNull()) {
            throw new Exception("Missing required parameter: message");
        }
        if (!arguments.get("message").isJsonPrimitive()) {
            throw new Exception("Parameter must be a string value");
        }
        JsonObject result = new JsonObject();
        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", arguments.get("message").getAsString());
        JsonArray contentArray = new JsonArray();
        contentArray.add(textContent);
        result.add("content", contentArray);
        result.addProperty("isError", false);
        return result;
    }
}