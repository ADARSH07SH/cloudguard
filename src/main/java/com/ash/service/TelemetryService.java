package com.ash.service;

import com.google.gson.JsonObject;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelemetryService {

    private final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public TelemetryService() {
        startTelemetryStream();
    }

    public void addClient(String clientId, SseEmitter emitter) {
        clients.put(clientId, emitter);
        emitter.onCompletion(() -> clients.remove(clientId));
        emitter.onTimeout(() -> clients.remove(clientId));
    }

    private void startTelemetryStream() {
        scheduler.scheduleAtFixedRate(() -> {
            if (clients.isEmpty()) {
                return;
            }

            JsonObject telemetryNotification = new JsonObject();
            telemetryNotification.addProperty("jsonrpc", "2.0");
            telemetryNotification.addProperty("method", "notifications/telemetry");

            JsonObject params = new JsonObject();
            params.addProperty("cpuUsage", Math.random() * 100);
            params.addProperty("memoryUsage", Math.random() * 100);
            params.addProperty("activeInstances", (int) (Math.random() * 50));
            telemetryNotification.add("params", params);

            String message = telemetryNotification.toString();

            clients.forEach((id, emitter) -> {
                try {
                    emitter.send(SseEmitter.event().name("message").data(message));
                } catch (IOException e) {
                    clients.remove(id);
                }
            });

        }, 0, 5, TimeUnit.SECONDS);
    }
    
    public void shutdown() {
        scheduler.shutdown();
    }
}
