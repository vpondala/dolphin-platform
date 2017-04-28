package com.canoo.dolphin.server.config;

import java.util.Collections;
import java.util.Map;

public class AdditionalTestConfigurationProvider implements ConfigurationProvider {

    public final static String PROPERTY_NAME = "someTestProperty";

    public final static String PROPERTY_VALUE = "Some Value";

    @Override
    public Map<String, String> getProperties() {
        return Collections.singletonMap(PROPERTY_NAME, PROPERTY_VALUE);
    }
}
