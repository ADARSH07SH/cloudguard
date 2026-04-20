package com.ash.tools;

import com.ash.protocol.McpHandler;
import com.google.gson.*;

public class ToolListHandler implements McpHandler {

    @Override
    public JsonElement handle(JsonObject params) {

        JsonArray tools = new JsonArray();

        JsonObject echoTool = new JsonObject();
        echoTool.addProperty("name", "echo_tool");
        echoTool.addProperty("description", "Echoes back input");
        JsonObject echoSchema = new JsonObject();
        echoSchema.addProperty("type", "object");
        JsonObject echoProps = new JsonObject();
        JsonObject message = new JsonObject();
        message.addProperty("type", "string");
        echoProps.add("message", message);
        echoSchema.add("properties", echoProps);
        echoTool.add("inputSchema", echoSchema);
        tools.add(echoTool);

        JsonObject listTool = new JsonObject();
        listTool.addProperty("name", "list_instances");
        listTool.addProperty("description", "Lists EC2 instances");
        JsonObject listSchema = new JsonObject();
        listSchema.addProperty("type", "object");
        listTool.add("inputSchema", listSchema);
        tools.add(listTool);

        JsonObject approveTool = new JsonObject();
        approveTool.addProperty("name", "approve_action");
        approveTool.addProperty("description", "Approves a pending action");
        JsonObject approveSchema = new JsonObject();
        approveSchema.addProperty("type", "object");
        JsonObject approveProps = new JsonObject();
        JsonObject actionId = new JsonObject();
        actionId.addProperty("type", "string");
        approveProps.add("action_id", actionId);
        approveSchema.add("properties", approveProps);
        approveTool.add("inputSchema", approveSchema);
        tools.add(approveTool);

        JsonObject deleteTool = new JsonObject();
        deleteTool.addProperty("name", "delete_instance");
        deleteTool.addProperty("description", "Deletes an EC2 instance (dummy)");
        JsonObject deleteSchema = new JsonObject();
        deleteSchema.addProperty("type", "object");
        JsonObject deleteProps = new JsonObject();
        JsonObject instanceId = new JsonObject();
        instanceId.addProperty("type", "string");
        deleteProps.add("id", instanceId);
        deleteSchema.add("properties", deleteProps);
        deleteTool.add("inputSchema", deleteSchema);
        tools.add(deleteTool);

        JsonObject result = new JsonObject();
        result.add("tools", tools);

        return result;
    }
}