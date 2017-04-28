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

import com.canoo.dolphin.server.event.impl.DefaultEventBusProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;

/**
 * This class defines the configuration of the Dolphin Platform. Normally the configuration is created based
 * on defaults and a property file (see {@link ConfigurationFileLoader}).
 */
public class DolphinPlatformConfiguration implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformConfiguration.class);

    public static final String OPEN_DOLPHIN_LOG_LEVEL = "openDolphinLogLevel";

    public static final String DOLPHIN_PLATFORM_SERVLET_MAPPING = "servletMapping";

    public static final String USE_CROSS_SITE_ORIGIN_FILTER = "useCrossSiteOriginFilter";

    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "accessControlAllowHeaders";

    public static final String ACCESS_CONTROL_ALLOW_METHODS = "accessControlAllowMethods";

    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "accessControlAllowCredentials";

    public static final String ACCESS_CONTROL_MAXAGE = "accessControlMaxAge";

    public static final String USE_SESSION_INVALIDATION_SERVLET = "useSessionInvalidationServlet";

    public static final String GARBAGE_COLLECTION_ACTIVE = "garbageCollectionActive";

    public static final String SESSION_TIMEOUT = "sessionTimeout";

    public static final String MAX_CLIENTS_PER_SESSION = "maxClientsPerSession";

    public static final String ID_FILTER_URL_MAPPINGS = "idFilterUrlMappings";

    public static final String ROOT_PACKAGE_FOR_CLASSPATH_SCAN = "rootPackageForClasspathScan";

    public static final String MBEAN_REGISTRATION = "mBeanRegistration";

    public static final String MAX_POLL_TIME = "maxPollTime";

    public static final String EVENTBUS_TYPE = "eventbusType";

    public static final String PLATFORM_ACTIVE = "active";





    private final static String USE_SESSION_INVALIDATION_SERVLET_DEFAULT_VALUE = "false";

    private final static String SESSION_TIMEOUT_DEFAULT_VALUE = "900";

    private final static String USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE = "true";

    private final static String M_BEAN_REGISTRATION_DEFAULT_VALUE = "false";

    private final static String DOLPHIN_PLATFORM_SERVLET_MAPPING_DEFAULT_VALUE = "/dolphin";

    private final static String ROOT_PACKAGE_FOR_CLASSPATH_SCAN_DEFAULT_VALUE = null;

    private final static String EVENTBUS_TYPE_DEFAULT_VALUE = DefaultEventBusProvider.DEFAULT_EVENTBUS_NAME;

    private final static String ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE = "/dolphin";

    private final static String OPEN_DOLPHIN_LOG_LEVEL_DEFAULT_VALUE = "SEVERE";

    private final static String MAX_POLL_TIME_DEFAULT_VALUE = "5000";

    private final static String MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE = "10";

    private final static String USE_GC_DEFAULT_VALUE = "true";

    private final static String ACTIVE_DEFAULT_VALUE = "true";

    private final static String ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE = "Content-Type,x-requested-with,origin,authorization,accept,client-security-token";

    private final static String ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE = "*";

    private final static String ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE = "true";

    private final static String ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE = "86400";

    private final Properties internalProperties;

    public DolphinPlatformConfiguration() {
        this(new Properties());
        setProperty(USE_SESSION_INVALIDATION_SERVLET, USE_SESSION_INVALIDATION_SERVLET_DEFAULT_VALUE);
        setProperty(SESSION_TIMEOUT, SESSION_TIMEOUT_DEFAULT_VALUE);
        setProperty(USE_CROSS_SITE_ORIGIN_FILTER, USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE);
        setProperty(MBEAN_REGISTRATION, M_BEAN_REGISTRATION_DEFAULT_VALUE);
        setProperty(DOLPHIN_PLATFORM_SERVLET_MAPPING, DOLPHIN_PLATFORM_SERVLET_MAPPING_DEFAULT_VALUE);
        setProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN, ROOT_PACKAGE_FOR_CLASSPATH_SCAN_DEFAULT_VALUE);
        setProperty(EVENTBUS_TYPE, EVENTBUS_TYPE_DEFAULT_VALUE);
        setProperty(ID_FILTER_URL_MAPPINGS, ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE);
        setProperty(OPEN_DOLPHIN_LOG_LEVEL, OPEN_DOLPHIN_LOG_LEVEL_DEFAULT_VALUE);
        setProperty(MAX_POLL_TIME, MAX_POLL_TIME_DEFAULT_VALUE);
        setProperty(MAX_CLIENTS_PER_SESSION, MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE);
        setProperty(GARBAGE_COLLECTION_ACTIVE, USE_GC_DEFAULT_VALUE);
        setProperty(PLATFORM_ACTIVE, ACTIVE_DEFAULT_VALUE);
        setProperty(ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE);
        setProperty(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE);
        setProperty(ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE);
        setProperty(ACCESS_CONTROL_MAXAGE, ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE);
    }

    public DolphinPlatformConfiguration(final Properties internalProperties) {
        this.internalProperties = internalProperties;
    }

    public boolean containsProperty(final String key) {
        return internalProperties.containsKey(key);
    }

    public String getProperty(final String key) {
        return getProperty(key, null);
    }

    public String getProperty(final String key, final String defaultValue) {
        return internalProperties.getProperty(key, defaultValue);
    }

    public Set<String> getPropertyKeys() {
        Set<String> ret = new HashSet<>();
        for(Object key : internalProperties.keySet()) {
            if(key != null) {
                ret.add(key.toString());
            }
        }
        return ret;
    }

    public void setProperty(final String key, final String value) {
        if(value == null) {
            LOG.warn("Setting property '{}' to null value will be ignored. Only value will be used if property is present");
        } else {
            internalProperties.setProperty(key, value);
        }
    }

    public int getSessionTimeout() {
        return Integer.parseInt(internalProperties.getProperty(SESSION_TIMEOUT, SESSION_TIMEOUT_DEFAULT_VALUE));
    }

    public boolean isUseCrossSiteOriginFilter() {
        return Boolean.parseBoolean(internalProperties.getProperty(USE_CROSS_SITE_ORIGIN_FILTER, USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE));
    }

    public String getDolphinPlatformServletMapping() {
        return internalProperties.getProperty(DOLPHIN_PLATFORM_SERVLET_MAPPING, DOLPHIN_PLATFORM_SERVLET_MAPPING_DEFAULT_VALUE);
    }

    public Level getOpenDolphinLogLevel() {
            String level = internalProperties.getProperty(OPEN_DOLPHIN_LOG_LEVEL, OPEN_DOLPHIN_LOG_LEVEL_DEFAULT_VALUE);
            if ("info".equalsIgnoreCase(level.trim())) {
                return Level.INFO;
            } else if ("severe".equalsIgnoreCase(level.trim())) {
                return Level.SEVERE;
            } else if ("all".equalsIgnoreCase(level.trim())) {
                return Level.ALL;
            } else if ("config".equalsIgnoreCase(level.trim())) {
                return Level.CONFIG;
            } else if ("fine".equalsIgnoreCase(level.trim())) {
                return Level.FINE;
            } else if ("finer".equalsIgnoreCase(level.trim())) {
                return Level.FINER;
            } else if ("finest".equalsIgnoreCase(level.trim())) {
                return Level.FINEST;
            } else if ("off".equalsIgnoreCase(level.trim())) {
                return Level.OFF;
            } else if ("warning".equalsIgnoreCase(level.trim())) {
                return Level.WARNING;
            }
            return Level.SEVERE;
    }

    public int getMaxClientsPerSession() {
        return Integer.parseInt(internalProperties.getProperty(MAX_CLIENTS_PER_SESSION, MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE));
    }

    public boolean isUseSessionInvalidationServlet() {
        return Boolean.parseBoolean(internalProperties.getProperty(USE_SESSION_INVALIDATION_SERVLET, USE_SESSION_INVALIDATION_SERVLET_DEFAULT_VALUE));
    }

    public List<String> getIdFilterUrlMappings() {
        return Arrays.asList(internalProperties.getProperty(ID_FILTER_URL_MAPPINGS, ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE).split(","));
    }

    public boolean isMBeanRegistration() {
        return Boolean.parseBoolean(internalProperties.getProperty(MBEAN_REGISTRATION, M_BEAN_REGISTRATION_DEFAULT_VALUE));
    }

    public String getRootPackageForClasspathScan() {
        return internalProperties.getProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN, ROOT_PACKAGE_FOR_CLASSPATH_SCAN_DEFAULT_VALUE);
    }

    public long getMaxPollTime() {
        return Long.parseLong(internalProperties.getProperty(MAX_POLL_TIME, MAX_POLL_TIME_DEFAULT_VALUE));
    }

    public boolean isUseGc() {
        return Boolean.parseBoolean(internalProperties.getProperty(GARBAGE_COLLECTION_ACTIVE, USE_GC_DEFAULT_VALUE));
    }

    public String getEventbusType() {
        return internalProperties.getProperty(EVENTBUS_TYPE, EVENTBUS_TYPE_DEFAULT_VALUE);
    }

    public List<String> getAccessControlAllowHeaders() {
        return Arrays.asList(internalProperties.getProperty(ACCESS_CONTROL_ALLOW_HEADERS, ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE).split(","));
    }

    public List<String> getAccessControlAllowMethods() {
        return Arrays.asList(internalProperties.getProperty(ACCESS_CONTROL_ALLOW_METHODS, ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE).split(","));
    }

    public boolean isAccessControlAllowCredentials() {
        return Boolean.parseBoolean(internalProperties.getProperty(ACCESS_CONTROL_ALLOW_CREDENTIALS, ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE));
    }

    public long getAccessControlMaxAge() {
        return Long.parseLong(internalProperties.getProperty(ACCESS_CONTROL_MAXAGE, ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE));
    }

    public boolean isActive() {
        return Boolean.parseBoolean(internalProperties.getProperty(PLATFORM_ACTIVE, ACTIVE_DEFAULT_VALUE));
    }

    public void log() {
        Set<Map.Entry<Object, Object>> properties = internalProperties.entrySet();
        for (Map.Entry property : properties) {
            LOG.debug("Dolphin Platform starts with value for " + property.getKey() + " = " + property.getValue());
        }
    }
}

