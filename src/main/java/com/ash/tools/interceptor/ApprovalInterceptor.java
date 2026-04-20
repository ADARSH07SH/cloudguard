package com.ash.tools.interceptor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ApprovalInterceptor implements ToolInterceptor {

    private static final String AUDIT_FILE = "audit.log";

    @Override
    public JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments) throws Exception {
        
        if (requiresApprovalCheck(toolName)) {
            if (!isApproved(toolName, arguments)) {
                throw new Exception("Action not approved. Use approve_action first.");
            }
        }

        return chain.proceed(toolName, arguments);
    }

    private boolean requiresApprovalCheck(String toolName) {
        return "delete_instance".equals(toolName);
    }

    private boolean isApproved(String toolName, JsonObject arguments) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(AUDIT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(toolName) && 
                    line.contains(arguments.toString()) && 
                    line.endsWith("APPROVED")) {
                    return true;
                }
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
}