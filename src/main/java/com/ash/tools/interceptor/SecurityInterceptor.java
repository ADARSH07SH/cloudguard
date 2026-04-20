package com.ash.tools.interceptor;

import com.ash.protocol.RequestContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Base64;

public class SecurityInterceptor implements ToolInterceptor {

    @Override
    public JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments, RequestContext context) throws Exception {
        
        if (context == null) {
            return chain.proceed(toolName, arguments, context);
        }
        
        String authHeader = context.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new Exception("Missing or invalid Authorization header");
        }
        
        String token = authHeader.substring(7);
        
        validateJWT(token, toolName);
        
        return chain.proceed(toolName, arguments, context);
    }

    private void validateJWT(String token, String toolName) throws Exception {
        
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new Exception("Invalid JWT format");
        }

        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

        System.out.println("JWT Header: " + header);
        System.out.println("JWT Payload: " + payload);

        JsonObject payloadJson = JsonParser.parseString(payload).getAsJsonObject();

        long exp = payloadJson.get("exp").getAsLong();
        long now = System.currentTimeMillis() / 1000;

        if (exp < now) {
            throw new Exception("Token expired");
        }

        if ("delete_instance".equals(toolName)) {
            if (!payloadJson.has("role") || !"admin".equals(payloadJson.get("role").getAsString())) {
                throw new Exception("Admin role required for delete operations");
            }
        }
    }
}