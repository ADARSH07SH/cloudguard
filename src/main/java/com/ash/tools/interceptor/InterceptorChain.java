package com.ash.tools.interceptor;

import com.ash.protocol.RequestContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface InterceptorChain {
    JsonElement proceed(String toolName, JsonObject arguments, RequestContext context) throws Exception;
}