package com.ash.tools.interceptor;

import com.ash.protocol.RequestContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ReadOnlyInterceptor implements ToolInterceptor {
    @Override
    public JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments, RequestContext context) throws Exception {
        return chain.proceed(toolName, arguments, context);
    }
}