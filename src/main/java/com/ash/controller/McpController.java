package com.ash.controller;

import com.ash.config.AppFactory;
import com.ash.protocol.Dispatcher;
import com.ash.protocol.RequestContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final Dispatcher dispatcher;

    public McpController() {
        this.dispatcher = AppFactory.createDispatcher();
    }

    private final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();

    @GetMapping(value="/events",produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(){
        SseEmitter emitter=new SseEmitter(Long.MAX_VALUE);
        String clientId="id_"+System.currentTimeMillis()+"sse_emitter"+System.currentTimeMillis();
        clients.put(clientId,emitter);
        emitter.onCompletion(() -> {clients.remove(clientId);});
        emitter.onTimeout(() -> {clients.remove(clientId);});
        return emitter;
    }

    @PostMapping("/message")
    public String handle(@RequestBody String message, @RequestHeader Map<String, String> headers) {

        try{
            RequestContext context = new RequestContext(headers);
            JsonObject request= JsonParser.parseString(message).getAsJsonObject();
            
            if (request.has("params") && request.get("params").isJsonObject()) {
                JsonObject params = request.getAsJsonObject("params");
                params.add("_context", new com.google.gson.Gson().toJsonTree(context));
            }
            
            JsonObject response= dispatcher.dispatch(request);

            return response.toString();
        }catch( Exception e){
            return "error"+e.getMessage();
        }
    }
}
