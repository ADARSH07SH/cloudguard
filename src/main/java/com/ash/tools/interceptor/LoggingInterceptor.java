package com.ash.tools.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LoggingInterceptor implements ToolInterceptor {
    @Override
    public JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments) throws Exception {
        System.out.println("Executing tool: " + toolName);
        JsonElement result = chain.proceed(toolName, arguments);
        System.out.println("Tool completed: " + toolName);
        return result;
    }
}