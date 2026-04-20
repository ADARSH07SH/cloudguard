package com.ash.tools.interceptor;

import com.ash.protocol.RequestContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.time.Duration;

public class MetricsInterceptor implements ToolInterceptor {
    
    private static final MeterRegistry meterRegistry = new SimpleMeterRegistry();
    
    @Override
    public JsonElement intercept(InterceptorChain chain, String toolName, JsonObject arguments, RequestContext context) throws Exception {
        
        Counter toolCounter = Counter.builder("tool.executions")
                .tag("tool", toolName)
                .register(meterRegistry);
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            JsonElement result = chain.proceed(toolName, arguments, context);
            
            toolCounter.increment();
            
            Counter.builder("tool.success")
                    .tag("tool", toolName)
                    .register(meterRegistry)
                    .increment();
            
            return result;
            
        } catch (Exception e) {
            Counter.builder("tool.errors")
                    .tag("tool", toolName)
                    .tag("error", e.getClass().getSimpleName())
                    .register(meterRegistry)
                    .increment();
            throw e;
        } finally {
            sample.stop(Timer.builder("tool.duration")
                    .tag("tool", toolName)
                    .register(meterRegistry));
        }
    }
    
    public static MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }
}