package com.ash.controller;

import com.ash.tools.interceptor.MetricsInterceptor;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    @GetMapping
    public Map<String, Object> getMetrics() {
        MeterRegistry registry = MetricsInterceptor.getMeterRegistry();
        Map<String, Object> metrics = new HashMap<>();
        
        registry.getMeters().forEach(meter -> {
            String name = meter.getId().getName();
            String tags = meter.getId().getTags().toString();
            
            switch (meter.getId().getType()) {
                case COUNTER:
                    metrics.put(name + tags, ((io.micrometer.core.instrument.Counter) meter).count());
                    break;
                case TIMER:
                    io.micrometer.core.instrument.Timer timer = (io.micrometer.core.instrument.Timer) meter;
                    metrics.put(name + ".count" + tags, timer.count());
                    metrics.put(name + ".total_time" + tags, timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS));
                    metrics.put(name + ".mean" + tags, timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS));
                    break;
            }
        });
        
        return metrics;
    }
    
    @GetMapping("/prometheus")
    public String getPrometheusMetrics() {
        MeterRegistry registry = MetricsInterceptor.getMeterRegistry();
        StringBuilder prometheus = new StringBuilder();
        
        registry.getMeters().forEach(meter -> {
            String name = meter.getId().getName().replace(".", "_");
            String tags = formatTags(meter.getId().getTags());
            
            switch (meter.getId().getType()) {
                case COUNTER:
                    prometheus.append("# TYPE ").append(name).append(" counter\n");
                    prometheus.append(name).append(tags).append(" ")
                            .append(((io.micrometer.core.instrument.Counter) meter).count()).append("\n");
                    break;
                case TIMER:
                    io.micrometer.core.instrument.Timer timer = (io.micrometer.core.instrument.Timer) meter;
                    prometheus.append("# TYPE ").append(name).append("_total counter\n");
                    prometheus.append(name).append("_total").append(tags).append(" ")
                            .append(timer.count()).append("\n");
                    prometheus.append("# TYPE ").append(name).append("_duration_sum counter\n");
                    prometheus.append(name).append("_duration_sum").append(tags).append(" ")
                            .append(timer.totalTime(java.util.concurrent.TimeUnit.SECONDS)).append("\n");
                    break;
            }
        });
        
        return prometheus.toString();
    }
    
    private String formatTags(Iterable<io.micrometer.core.instrument.Tag> tags) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (io.micrometer.core.instrument.Tag tag : tags) {
            if (first) {
                sb.append("{");
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(tag.getKey()).append("=\"").append(tag.getValue()).append("\"");
        }
        if (!first) {
            sb.append("}");
        }
        return sb.toString();
    }
}