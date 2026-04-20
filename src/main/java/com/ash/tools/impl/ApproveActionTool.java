package com.ash.tools.impl;

import com.ash.tools.Tool;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ApproveActionTool implements Tool {

    private static final String FILE_NAME = "audit.log";

    @Override
    public JsonElement execute(JsonObject arguments) throws Exception {

        if (arguments == null || !arguments.has("action_id")) {
            throw new Exception("action_id is required");
        }

        String actionId = arguments.get("action_id").getAsString();

        File file = new File(FILE_NAME);

        if (!file.exists()) {
            throw new Exception("audit.log not found");
        }

        List<String> lines = new ArrayList<>();
        boolean found = false;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;

        while ((line = reader.readLine()) != null) {

            if (line.startsWith(actionId)) {
                line = line.replace("PENDING", "APPROVED");
                found = true;
            }

            lines.add(line);
        }

        reader.close();

        if (!found) {
            throw new Exception("Action ID not found: " + actionId);
        }


        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        for (String l : lines) {
            writer.write(l);
            writer.newLine();
        }
        writer.close();


        JsonObject result = new JsonObject();
        JsonArray content = new JsonArray();

        JsonObject text = new JsonObject();
        text.addProperty("type", "text");
        text.addProperty("text", "Action " + actionId + " approved");

        content.add(text);

        result.add("content", content);
        result.addProperty("isError", false);

        return result;
    }
}