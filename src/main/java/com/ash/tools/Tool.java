package com.ash.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface Tool {
    String getName();
    JsonElement execute(JsonObject arguments)throws Exception;
}
