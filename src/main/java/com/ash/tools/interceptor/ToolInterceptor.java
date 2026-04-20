package com.ash.tools.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface ToolInterceptor {
    JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments) throws Exception;
}