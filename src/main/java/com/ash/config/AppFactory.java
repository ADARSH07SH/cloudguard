package com.ash.config;

import com.ash.lifecycle.InitializeHandler;
import com.ash.lifecycle.InitializedHandler;
import com.ash.protocol.Dispatcher;
import com.ash.tools.CallToolHandler;
import com.ash.tools.ToolListHandler;
import com.ash.tools.ToolRegistry;
import com.ash.tools.impl.ApproveActionTool;
import com.ash.tools.impl.DeleteInstanceTool;
import com.ash.tools.impl.EchoTool;
import com.ash.tools.impl.ListInstancesTool;
import com.ash.tools.interceptor.*;

import java.util.ArrayList;
import java.util.List;

public class AppFactory {

     public static Dispatcher createDispatcher(){
         Dispatcher dispatcher=new Dispatcher();
         ToolRegistry toolRegistry=new ToolRegistry();

         List<ToolInterceptor> interceptors = new ArrayList<>();
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
         toolRegistry.registerTool("approve_action", new ApproveActionTool());
         toolRegistry.registerTool("delete_instance", new DeleteInstanceTool());

         return dispatcher;
     }
}
