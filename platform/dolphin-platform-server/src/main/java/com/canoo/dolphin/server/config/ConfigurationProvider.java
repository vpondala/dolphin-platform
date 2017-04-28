package com.canoo.dolphin.server.config;

import java.util.Map;

public interface ConfigurationProvider {

    Map<String, String> getProperties();

}
