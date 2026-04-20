package com.ash.cloud;

public class Instance {
    private String id;
    private String type;
    private String state;
    private String provider;

    public Instance(String id, String type, String state, String provider) {
        this.id = id;
        this.type = type;
        this.state = state;
        this.provider = provider;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getState() { return state; }
    public String getProvider() { return provider; }
}