package com.ash.protocol;

import java.util.Map;

public class RequestContext {
    private Map<String, String> headers;

    public RequestContext(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }
}