package com.ash.tools.interceptor;

import com.ash.protocol.RequestContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface ToolInterceptor {
    JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments, RequestContext context) throws Exception;
}