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
package com.canoo.dolphin.server.spring;

import com.canoo.dolphin.server.bootstrap.DolphinPlatformBootstrap;
import com.canoo.dolphin.server.config.ConfigurationFileLoader;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Basic Bootstrap for Spring based application. The bootstrap automatically starts the dolphin platform bootstrap.
 *
 * @author Hendrik Ebbers
 */
@Configuration
public class DolphinPlatformSpringBootstrap implements ServletContextInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformSpringBootstrap.class);

    private static final String PREFIX = "dolphinPlatform.";

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private DolphinPlatformConfiguration injectedConfig;

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        DolphinPlatformConfiguration configuration = injectedConfig;
        if(configuration == null) {
            configuration = ConfigurationFileLoader.loadConfiguration();
        }
        updateConfigurationBySpring(configuration);
        if(configuration.isActive()) {
            final DolphinPlatformBootstrap bootstrap = new DolphinPlatformBootstrap(servletContext, configuration);
            bootstrap.start();
        }
    }

    private void updateConfigurationBySpring(DolphinPlatformConfiguration configuration) {
        for(String key : configuration.getPropertyKeys()) {
            String valInSpringConfig = environment.getProperty(PREFIX + key);
            if(valInSpringConfig != null) {
                LOG.debug("Dolphin Platform property '{}' found in spring configuration", key);
                configuration.setProperty(key, valInSpringConfig);
            }
        }
    }
}
