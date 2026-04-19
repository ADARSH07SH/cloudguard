package com.ash.protocol;

import com.google.gson.*;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {

    private final Map<String, McpHandler> handlers = new HashMap<>();

    public void registerHandler(String method, McpHandler handler) {
        handlers.put(method, handler);
    }

    public JsonObject dispatch(JsonObject request) {

        if (request == null || !request.has("method") || request.get("method").isJsonNull()) {
            return buildError(request, -32600, "Invalid Request");
        }

        String method;
        try {
            method = request.get("method").getAsString();
        } catch (Exception e) {
            return buildError(request, -32600, "Invalid Request");
        }

        JsonObject params = request.has("params") && request.get("params").isJsonObject()
                ? request.getAsJsonObject("params")
                : new JsonObject();

        McpHandler handler = handlers.get(method);

        if (handler == null) {
            if (!request.has("id")) return null;
            return buildError(request, -32601, "Method not found");
        }

        try {
            JsonElement result = handler.handle(params);

            if (!request.has("id")) {
                return null;
            }

            JsonObject response = new JsonObject();
            response.addProperty("jsonrpc", "2.0");
            response.add("id", request.get("id"));
            response.add("result", result == null ? JsonNull.INSTANCE : result);

            return response;

        } catch (Exception e) {
            if (!request.has("id")) return null;
            return buildError(request, -32603, e.getMessage());
        }
    }

    private JsonObject buildError(JsonObject request, int code, String message) {

        if (request == null || !request.has("id")) return null;

        JsonObject error = new JsonObject();
        error.addProperty("jsonrpc", "2.0");
        error.add("id", request.get("id"));

        JsonObject err = new JsonObject();
        err.addProperty("code", code);
        err.addProperty("message", message);

        error.add("error", err);
        return error;
    }
}