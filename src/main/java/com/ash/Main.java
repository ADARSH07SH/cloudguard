package com.ash;

import com.ash.protocol.Dispatcher;
import com.ash.lifecycle.InitializeHandler;
import com.ash.lifecycle.InitializedHandler;
import com.ash.tools.CallToolHandler;
import com.ash.tools.ToolListHandler;
import com.ash.tools.ToolRegistry;
import com.ash.tools.impl.EchoTool;
import com.google.gson.*;

import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Main {

    public static void main(String[] args) throws Exception {

        Dispatcher dispatcher = new Dispatcher();
        ToolRegistry toolRegistry = new ToolRegistry();

        dispatcher.registerHandler("initialize", new InitializeHandler());
        dispatcher.registerHandler("notifications/initialized", new InitializedHandler());
        dispatcher.registerHandler("tools/list", new ToolListHandler());
        dispatcher.registerHandler("tools/call",new CallToolHandler(toolRegistry));

        toolRegistry.registerTool("echo_tool",new EchoTool());
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Gson gson = new Gson();

        StringBuilder sb=new StringBuilder();
        char[]buffer=new char[1024];
        int totalRead=0;
        int MAX_SIZE=1024*1024;
        int n;

        while((n=reader.read(buffer))!=-1){
            totalRead+=n;

            if(totalRead>MAX_SIZE){
                System.err.println(totalRead+" is too large");
                break;
            }

            sb.append(buffer,0,totalRead);

            if(sb.toString().trim().endsWith("}")){
                JsonObject request=gson.fromJson(sb.toString(),JsonObject.class);

                JsonObject response=dispatcher.dispatch(request);
                if(response!=null){
                    System.out.println(gson.toJson(response));
                }
                sb.setLength(0);
                totalRead=0;
            }
        }
    }
}