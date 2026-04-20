package com.ash.tools;

import com.ash.protocol.McpHandler;
import com.ash.protocol.RequestContext;
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
        listTool.addProperty("description", "Lists instances from specified cloud provider (aws/gcp/azure)");
        JsonObject listSchema = new JsonObject();
        listSchema.addProperty("type", "object");
        JsonObject listProps = new JsonObject();
        JsonObject providerProp = new JsonObject();
        providerProp.addProperty("type", "string");
        providerProp.addProperty("enum", "[\"aws\", \"gcp\", \"azure\"]");
        listProps.add("provider", providerProp);
        listSchema.add("properties", listProps);
        listTool.add("inputSchema", listSchema);
        tools.add(listTool);

        JsonObject listAllTool = new JsonObject();
        listAllTool.addProperty("name", "list_all_instances");
        listAllTool.addProperty("description", "Lists instances from all cloud providers");
        JsonObject listAllSchema = new JsonObject();
        listAllSchema.addProperty("type", "object");
        listAllTool.add("inputSchema", listAllSchema);
        tools.add(listAllTool);

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
        deleteTool.addProperty("description", "Deletes an instance from specified cloud provider");
        JsonObject deleteSchema = new JsonObject();
        deleteSchema.addProperty("type", "object");
        JsonObject deleteProps = new JsonObject();
        JsonObject instanceId = new JsonObject();
        instanceId.addProperty("type", "string");
        deleteProps.add("id", instanceId);
        JsonObject deleteProvider = new JsonObject();
        deleteProvider.addProperty("type", "string");
        deleteProvider.addProperty("enum", "[\"aws\", \"gcp\", \"azure\"]");
        deleteProps.add("provider", deleteProvider);
        deleteSchema.add("properties", deleteProps);
        deleteTool.add("inputSchema", deleteSchema);
        tools.add(deleteTool);

        JsonObject result = new JsonObject();
        result.add("tools", tools);

        return result;
    }
}