package com.ash.tools;

import com.ash.protocol.McpHandler;
import com.ash.protocol.RequestContext;
import com.ash.tools.interceptor.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class CallToolHandler implements McpHandler {

    private final ToolRegistry toolRegistry;
    private final List<ToolInterceptor> interceptors;

    public CallToolHandler(ToolRegistry toolRegistry, List<ToolInterceptor> interceptors) {
        this.toolRegistry = toolRegistry;
        this.interceptors = interceptors;
    }

    @Override
    public JsonElement handle(JsonObject params) throws Exception {
        if (params == null) {
            throw new Exception("invalid params");
        }
        if(!params.has("name")||params.get("name").isJsonNull()){
            throw new Exception("Tool name required");
        }

        String name = params.get("name").getAsString();

        JsonObject argument = params.has("arguments")&&params.get("arguments").isJsonObject()
                ?params.getAsJsonObject("arguments")
                :new JsonObject();

        RequestContext context = null;
        if (params.has("_context") && params.get("_context").isJsonObject()) {
            JsonObject contextObj = params.getAsJsonObject("_context");
            java.util.Map<String, String> headers = new java.util.HashMap<>();
            if (contextObj.has("headers") && contextObj.get("headers").isJsonObject()) {
                JsonObject headersObj = contextObj.getAsJsonObject("headers");
                for (String key : headersObj.keySet()) {
                    headers.put(key, headersObj.get(key).getAsString());
                }
            }
            context = new RequestContext(headers);
        }

        InterceptorChain chain=new InterceptorChainImpl(interceptors,0,toolRegistry);

        return chain.proceed(name,argument, context);
    }

}

