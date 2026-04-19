package com.ash.tools.impl;

import com.ash.tools.Tool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


public class EchoTool implements Tool {
    @Override
        public JsonElement execute(JsonObject arguments) throws Exception {

            if(!arguments.has("message")||arguments.get("message").isJsonNull()){
                throw new Exception("message is required");
            }

            if(!arguments.get("message").isJsonPrimitive()){
                throw new Exception("message is required");
            }


            JsonObject result = new JsonObject();


            JsonObject textContent = new JsonObject();
            textContent.addProperty("type", "text");
            textContent.addProperty("text", arguments.get("message").getAsString());

            JsonArray contentArray = new JsonArray();
            contentArray.add(textContent);

            result.add("content", contentArray);
            result.addProperty("isError", false);

            return result;
        }

}
