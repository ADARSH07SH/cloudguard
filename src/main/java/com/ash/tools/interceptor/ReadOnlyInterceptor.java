package com.ash.tools.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ReadOnlyInterceptor implements ToolInterceptor {
    @Override
    public JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments) throws Exception {
        return chain.proceed(toolName, arguments);
    }
}