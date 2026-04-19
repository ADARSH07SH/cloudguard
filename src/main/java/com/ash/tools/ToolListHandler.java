package com.ash.tools;

import com.ash.protocol.McpHandler;
import com.google.gson.*;

public class ToolListHandler implements McpHandler {

    @Override
    public JsonElement handle(JsonObject params) {

        JsonArray tools = new JsonArray();

        JsonObject echoTool = new JsonObject();
        echoTool.addProperty("name", "echo_tool");
        echoTool.addProperty("description", "Echoes back input");

        JsonObject inputSchema = new JsonObject();
        inputSchema.addProperty("type", "object");

        JsonObject properties = new JsonObject();
        JsonObject message = new JsonObject();
        message.addProperty("type", "string");

        properties.add("message", message);
        inputSchema.add("properties", properties);

        echoTool.add("inputSchema", inputSchema);

        tools.add(echoTool);

        JsonObject result = new JsonObject();
        result.add("tools", tools);

        return result;
    }
}