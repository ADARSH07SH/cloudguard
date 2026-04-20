package com.ash.tools;

import com.ash.protocol.McpHandler;
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

        InterceptorChain chain=new InterceptorChainImpl(interceptors,0,toolRegistry);

        return chain.proceed(name,argument);
    }

}

