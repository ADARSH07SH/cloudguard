package com.ash.cloud;

import java.util.List;

public interface CloudProvider {
    List<Instance> listInstances() throws Exception;
    void deleteInstance(String instanceId) throws Exception;
    String getProviderName();
}