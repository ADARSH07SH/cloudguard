package com.ash.controller;

import com.ash.config.AppFactory;
import com.ash.protocol.Dispatcher;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import software.amazon.eventstream.Message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final Dispatcher dispatcher;

    public McpController() {
        this.dispatcher = AppFactory.createDispatcher();
    }

    private final List<SseEmitter>clients=new CopyOnWriteArrayList<>();

    @GetMapping(value="/events",produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(){
        SseEmitter emitter=new SseEmitter(Long.MAX_VALUE);
        clients.add(emitter);
        emitter.onCompletion(() -> {clients.remove(emitter);});
        emitter.onTimeout(() -> {clients.remove(emitter);});
        return emitter;
    }

    @PostMapping("/message")
    public String handle(@RequestBody String message) {

        try{
            JsonObject request= JsonParser.parseString(message).getAsJsonObject();
            JsonObject response= dispatcher.dispatch(request);

            return response.toString();
        }catch( Exception e){
            return "error"+e.getMessage();
        }
    }

}
