package com.ash.tools;

import com.ash.protocol.McpHandler;
import com.ash.tools.impl.EchoTool;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CallToolHandler implements McpHandler {

    private final ToolRegistry toolRegistry;

    public CallToolHandler(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
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

        Tool tool=toolRegistry.getTool(name);

        if(tool==null){
            throw new Exception("No such tool "+name);
        }

        return tool.execute(argument);
    }

}
