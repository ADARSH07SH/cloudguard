package com.ash.tools;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ToolRegistry {

    Map<String,Tool> tools=new HashMap<String,Tool>();

    public void registerTool(String name,Tool tool){
        tools.put(name,tool);
    }
    public Tool getTool(String name){
        return tools.get(name);
    }
}
