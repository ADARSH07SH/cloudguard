package com.ash.protocol;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface McpHandler {
    //returns the result object on a sucessful json-rpc call

    JsonElement handle(JsonObject params) throws Exception;
}
