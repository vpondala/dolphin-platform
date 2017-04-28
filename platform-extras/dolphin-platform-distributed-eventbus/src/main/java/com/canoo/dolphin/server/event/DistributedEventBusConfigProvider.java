package com.canoo.dolphin.server.event;

import com.canoo.dolphin.server.config.ConfigurationProvider;

import java.util.HashMap;
import java.util.Map;

public class DistributedEventBusConfigProvider implements ConfigurationProvider {

    public static final String HAZELCAST_SERVER_NAME = "hazelcast.server.name";

    public static final String HAZELCAST_SERVER_PORT = "hazelcast.server.port";

    public static final String HAZELCAST_GROUP_NAME = "hazelcast.group.name";

    public static final String DEFAULT_HAZELCAST_SERVER = "localhost";

    public static final String DEFAULT_HAZELCAST_PORT = "5701";

    public static final String DEFAULT_HAZELCAST_GROUP_NAME = "micro-landscape";

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> properties = new HashMap<>();

        properties.put(HAZELCAST_SERVER_NAME, DEFAULT_HAZELCAST_SERVER);
        properties.put(HAZELCAST_SERVER_PORT, DEFAULT_HAZELCAST_PORT);
        properties.put(HAZELCAST_GROUP_NAME, DEFAULT_HAZELCAST_GROUP_NAME);
        return properties;
    }
}
