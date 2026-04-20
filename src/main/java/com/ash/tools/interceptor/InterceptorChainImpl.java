package com.ash.tools.interceptor;

import com.ash.tools.Tool;
import com.ash.tools.ToolRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class InterceptorChainImpl implements InterceptorChain {
    private final List<ToolInterceptor> interceptors;
    private final int index;
    private final ToolRegistry toolRegistry;

    public InterceptorChainImpl(List<ToolInterceptor> interceptors, int index, ToolRegistry toolRegistry) {
        this.interceptors = interceptors;
        this.index = index;
        this.toolRegistry = toolRegistry;
    }

    @Override
        public JsonElement proceed(String toolName, JsonObject arguments) throws Exception {
            if (index < interceptors.size()) {
                ToolInterceptor interceptor = interceptors.get(index);
                InterceptorChain nextChain = new InterceptorChainImpl(interceptors, index + 1, toolRegistry);
                return interceptor.intercept(nextChain, toolName, arguments);
            } else {
                if (toolName == null || toolName.trim().isEmpty()) {
                    throw new Exception("Tool name cannot be null or empty");
                }

                Tool tool = toolRegistry.getTool(toolName);
                if (tool == null) {
                    throw new Exception("Tool not found: " + toolName);
                }

                if (arguments == null) {
                    arguments = new JsonObject();
                }

                return tool.execute(arguments);
            }
        }

}