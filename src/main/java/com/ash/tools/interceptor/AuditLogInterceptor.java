package com.ash.tools.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class AuditLogInterceptor implements ToolInterceptor {

    private static final String AUDIT_FILE = "audit.log";

    @Override
    public JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments) throws Exception {
        
        if (requiresApproval(toolName)) {
            String actionId = generateActionId();
            logPendingAction(actionId, toolName, arguments);
            throw new Exception("Approval required for actionId: " + actionId);
        }

        return chain.proceed(toolName, arguments);
    }

    private boolean requiresApproval(String toolName) {
        return "delete_instance".equals(toolName);
    }

    private String generateActionId() {
        return "A" + UUID.randomUUID().toString().replace("-", "").substring(0, 9).toUpperCase();
    }

    private void logPendingAction(String actionId, String toolName, JsonObject arguments) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AUDIT_FILE, true))) {
            writer.write(actionId + " | " + toolName + " | " + arguments.toString() + " | PENDING");
            writer.newLine();
        }
    }
}