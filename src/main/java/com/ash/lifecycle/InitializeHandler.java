package com.ash.lifecycle;

import com.ash.protocol.McpHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitializeHandler implements McpHandler {

    @Override
    public JsonElement handle(JsonObject params) {

        JsonObject result = new JsonObject();

        result.addProperty("protocolVersion", "2024-11-05");

        JsonObject capabilities = new JsonObject();
        JsonObject tools = new JsonObject();
        capabilities.add("tools", tools);

        result.add("capabilities", capabilities);

        JsonObject serverInfo = new JsonObject();
        serverInfo.addProperty("name", "cloudguard");
        serverInfo.addProperty("version", "1.0.0");

        result.add("serverInfo", serverInfo);

        return result;
    }
}