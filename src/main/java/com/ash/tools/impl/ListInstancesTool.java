package com.ash.tools.impl;

import com.ash.tools.Tool;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Reservation;

public class ListInstancesTool implements Tool {

    @Override
    public JsonElement execute(JsonObject arguments) throws Exception {

        Ec2Client ec2Client = Ec2Client.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        DescribeInstancesResponse response = ec2Client.describeInstances();

        StringBuilder sb = new StringBuilder();

        for (Reservation reservation : response.reservations()) {
            for (Instance instance : reservation.instances()) {

                String id = instance.instanceId();
                String type = instance.instanceTypeAsString();
                String state = instance.state().nameAsString();

                sb.append(id)
                        .append(" (")
                        .append(type)
                        .append(") - ")
                        .append(state)
                        .append("\n");
            }
        }

        if (sb.length() == 0) {
            sb.append("No instances found");
        }

        JsonObject result = new JsonObject();

        JsonObject textObj = new JsonObject();
        textObj.addProperty("type", "text");
        textObj.addProperty("text", sb.toString());

        result.add("content", new com.google.gson.JsonArray());
        result.getAsJsonArray("content").add(textObj);

        result.addProperty("isError", false);

        return result;
    }
}
