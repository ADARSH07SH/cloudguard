package com.ash.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface McpHandler {

    JsonElement handle(JsonObject params) throws Exception;
}