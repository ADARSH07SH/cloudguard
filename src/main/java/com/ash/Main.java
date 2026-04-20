package com.ash;

import com.ash.protocol.Dispatcher;
import com.ash.lifecycle.InitializeHandler;
import com.ash.lifecycle.InitializedHandler;
import com.ash.tools.CallToolHandler;
import com.ash.tools.ToolListHandler;
import com.ash.tools.ToolRegistry;
import com.ash.tools.impl.EchoTool;
import com.ash.tools.impl.ListInstancesTool;
import com.ash.tools.impl.ListAllCloudInstancesTool;
import com.ash.tools.impl.ApproveActionTool;
import com.ash.tools.impl.DeleteInstanceTool;
import com.ash.tools.interceptor.ToolInterceptor;
import com.ash.tools.interceptor.SecurityInterceptor;
import com.ash.tools.interceptor.MetricsInterceptor;
import com.ash.tools.interceptor.LoggingInterceptor;
import com.ash.tools.interceptor.AuditLogInterceptor;
import com.ash.tools.interceptor.ApprovalInterceptor;
import com.ash.tools.interceptor.ReadOnlyInterceptor;
import com.ash.protocol.RequestContext;
import com.ash.config.AppConfig;
import com.google.gson.*;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {

        Dispatcher dispatcher = new Dispatcher();
        ToolRegistry toolRegistry = new ToolRegistry();

        List<ToolInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new SecurityInterceptor());
        interceptors.add(new MetricsInterceptor());
        interceptors.add(new LoggingInterceptor());
        interceptors.add(new AuditLogInterceptor());
        interceptors.add(new ApprovalInterceptor());
        interceptors.add(new ReadOnlyInterceptor());

        dispatcher.registerHandler("initialize", new InitializeHandler());
        dispatcher.registerHandler("notifications/initialized", new InitializedHandler());
        dispatcher.registerHandler("tools/list", new ToolListHandler());
        dispatcher.registerHandler("tools/call", new CallToolHandler(toolRegistry, interceptors));

        toolRegistry.registerTool("echo_tool", new EchoTool());
        toolRegistry.registerTool("list_instances", new ListInstancesTool());
        toolRegistry.registerTool("list_all_instances", new ListAllCloudInstancesTool());
        toolRegistry.registerTool("approve_action", new ApproveActionTool());
        toolRegistry.registerTool("delete_instance", new DeleteInstanceTool());

        Gson gson = new Gson();
        final int MAX_LINE_LENGTH = AppConfig.MAX_LINE_LENGTH;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            StringBuilder lineBuilder = new StringBuilder();
            int ch;

            while ((ch = reader.read()) != -1) {
                if (ch == '\n') {
                    String line = lineBuilder.toString().trim();
                    if (!line.isEmpty()) {
                        try {
                            JsonObject request = gson.fromJson(line, JsonObject.class);
                            JsonObject response = dispatcher.dispatch(request);

                            if (response != null) {
                                System.out.println(gson.toJson(response));
                            }
                        } catch (JsonSyntaxException e) {
                            System.err.println("Invalid JSON received: " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Error processing request: " + e.getMessage());
                        }
                    }
                    lineBuilder.setLength(0);
                } else if (ch == '\r') {
                    continue;
                } else {
                    lineBuilder.append((char) ch);

                    if (lineBuilder.length() > MAX_LINE_LENGTH) {
                        System.err.println("Line too long, discarding");
                        lineBuilder.setLength(0);
                    }
                }
            }
        }
    }
}