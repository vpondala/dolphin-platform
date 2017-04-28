/*
 * Copyright 2015-2017 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.config;

import com.canoo.dolphin.server.bootstrap.DolphinPlatformBoostrapException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;

/**
 * This class loads a Dolphin Platform configuration (see {@link DolphinPlatformConfiguration}) based on a property file.
 * The file must be placed under "META-INF/dolphin.properties" (normal for a JAR) or under
 * "WEB-INF/classes/META-INF/dolphin.properties" (normal for a WAR). If no file can be found a default
 * confihuration will be returned.
 * <p>
 * Currently the following properties will be supported in the "dolphin.properties" file
 * <p>
 * - openDolphinLogLevel that defines the level of the remoting logging (supported: "INFO", "WARN", etc.)
 * - servletMapping that defines the endpoint of the Dolphin Platform servlet
 * - useCrossSiteOriginFilter (true / false) that defines if a cross site origin filter should be used
 * <p>
 * All properties that are not specified in the property file will be defined by default values.
 */
public class ConfigurationFileLoader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFileLoader.class);

    private static final String JAR_LOCATION = "META-INF/dolphin.properties";

    private static final String WAR_LOCATION = "WEB-INF/classes/" + JAR_LOCATION;

    private ConfigurationFileLoader() {
    }

    /**
     * Tries to load a {@link DolphinPlatformConfiguration} based on a file. if no config file
     * can be found a default config will be returned.
     *
     * @return a configuration
     */
    public static DolphinPlatformConfiguration loadConfiguration() {
        DolphinPlatformConfiguration configuration = createConfiguration();

        ServiceLoader<ConfigurationProvider> serviceLoader = ServiceLoader.load(ConfigurationProvider.class);
        for(ConfigurationProvider provider : serviceLoader) {
            Map<String, String> additionalProperties = provider.getProperties();
            for(Map.Entry<String, String> property : additionalProperties.entrySet()) {
                if(!configuration.containsProperty(property.getKey())) {
                    configuration.setProperty(property.getKey(), property.getValue());
                }
            }
        }
        LOG.debug("Configuration created with {} properties", configuration.getPropertyKeys().size());
        if(LOG.isTraceEnabled()) {
            for(String key : configuration.getPropertyKeys()) {
                LOG.debug("Dolphin Platform configured with '{}'='{}'", key, configuration.getProperty(key));
            }
        }

        return configuration;
    }

    private static DolphinPlatformConfiguration createConfiguration() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            try (InputStream inputStream = classLoader.getResourceAsStream(JAR_LOCATION)) {
                if (inputStream != null) {
                    return readConfig(inputStream);
                }
            }

            try (InputStream inputStream = classLoader.getResourceAsStream(WAR_LOCATION)) {
                if (inputStream == null) {
                    LOG.info("Can not read configuration. Maybe no dolphin.properties file is defined. Will use a default configuration!");
                    return new DolphinPlatformConfiguration();
                } else {
                    return readConfig(inputStream);
                }
            }
        } catch (IOException e) {
            throw new DolphinPlatformBoostrapException("Can not create configuration!", e);
        }
    }

    private static DolphinPlatformConfiguration readConfig(InputStream input) throws IOException {
        Properties prop = new Properties();
        prop.load(input);

        return  new DolphinPlatformConfiguration(prop);
    }
}
