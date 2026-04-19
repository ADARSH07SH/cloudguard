package com.ash.lifecycle;

import com.ash.protocol.McpHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitializedHandler implements McpHandler {

    @Override
    public JsonElement handle(JsonObject params) {
        System.out.println("Client initialized");
        return null;
    }
}