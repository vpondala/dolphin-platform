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
package com.canoo.dolphin.server.bootstrap;

import com.canoo.dolphin.server.DolphinListener;
import com.canoo.dolphin.server.DolphinSession;
import com.canoo.dolphin.server.DolphinSessionListener;
import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.DefaultDolphinContextFactory;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextCommunicationHandler;
import com.canoo.dolphin.server.context.DolphinContextFactory;
import com.canoo.dolphin.server.context.DolphinContextFilter;
import com.canoo.dolphin.server.context.DolphinContextUtils;
import com.canoo.dolphin.server.context.DolphinHttpSessionListener;
import com.canoo.dolphin.server.context.DolphinSessionLifecycleHandler;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.event.DolphinEventBus;
import com.canoo.dolphin.server.event.impl.EventBusProvider;
import com.canoo.dolphin.server.impl.ClasspathScanner;
import com.canoo.dolphin.server.mbean.MBeanRegistry;
import com.canoo.dolphin.server.servlet.CrossSiteOriginFilter;
import com.canoo.dolphin.server.servlet.DolphinPlatformServlet;
import com.canoo.dolphin.server.servlet.InvalidationServlet;
import com.canoo.dolphin.util.Assert;
import com.canoo.dolphin.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Set;

import static com.canoo.dolphin.server.servlet.ServletConstants.DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING;
import static com.canoo.dolphin.server.servlet.ServletConstants.DOLPHIN_CLIENT_ID_FILTER_NAME;
import static com.canoo.dolphin.server.servlet.ServletConstants.DOLPHIN_CROSS_SITE_FILTER_NAME;
import static com.canoo.dolphin.server.servlet.ServletConstants.DOLPHIN_INVALIDATION_SERVLET_NAME;
import static com.canoo.dolphin.server.servlet.ServletConstants.DOLPHIN_SERVLET_NAME;

/**
 * This class defines the bootstrap for Dolphin Platform.
 */
public class DolphinPlatformBootstrap {

    private static final String CONFIGURATION_ATTRIBUTE_NAME = "dolphinPlatformConfiguration";

    private static final Logger LOG = LoggerFactory.getLogger(DolphinPlatformBootstrap.class);

    private static final DolphinSessionProvider sessionProvider = new DolphinSessionProvider() {

        @Override
        public DolphinSession getCurrentDolphinSession() {
            DolphinContext context = DolphinContextUtils.getContextForCurrentThread();
            if (context == null) {
                return null;
            }
            return context.getDolphinSession();
        }

    };

    private static final DolphinSessionLifecycleHandler sessionLifecycleHandler = new DolphinSessionLifecycleHandler();

    private final ServletContext servletContext;

    private final DolphinPlatformConfiguration configuration;

    public DolphinPlatformBootstrap(ServletContext servletContext, final DolphinPlatformConfiguration configuration) {
        this.servletContext = Assert.requireNonNull(servletContext, "servletContext");
        this.configuration = Assert.requireNonNull(configuration, "configuration");

        servletContext.setAttribute(CONFIGURATION_ATTRIBUTE_NAME, configuration);

        configuration.log();
    }

    /**
     * This methods starts the Dolphin Platform server runtime
     */
    public void start() {
        LOG.info("Starting Dolphin Platform");

        final ClasspathScanner classpathScanner = new ClasspathScanner(configuration.getRootPackageForClasspathScan());

        MBeanRegistry.getInstance().setMbeanSupport(configuration.isMBeanRegistration());

        final ContainerManager containerManager = findManager();
        containerManager.init(servletContext);

        registerDolphinSessionListener(containerManager, classpathScanner);

        final DolphinContextCommunicationHandler communicationHandler = new DolphinContextCommunicationHandler(configuration);

        DolphinContextFactory dolphinContextFactory = new DefaultDolphinContextFactory(configuration, getSessionProvider(), containerManager, classpathScanner, sessionLifecycleHandler);
        servletContext.addServlet(DOLPHIN_SERVLET_NAME, new DolphinPlatformServlet(communicationHandler)).addMapping(configuration.getDolphinPlatformServletMapping());
        if (configuration.isUseSessionInvalidationServlet()) {
            servletContext.addServlet(DOLPHIN_INVALIDATION_SERVLET_NAME, new InvalidationServlet()).addMapping(DEFAULT_DOLPHIN_INVALIDATION_SERVLET_MAPPING);
        }
        if (configuration.isUseCrossSiteOriginFilter()) {
            servletContext.addFilter(DOLPHIN_CROSS_SITE_FILTER_NAME, new CrossSiteOriginFilter()).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }

        servletContext.addFilter(DOLPHIN_CLIENT_ID_FILTER_NAME, new DolphinContextFilter(configuration, containerManager, dolphinContextFactory, sessionLifecycleHandler)).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, configuration.getIdFilterUrlMappings().toArray(new String[configuration.getIdFilterUrlMappings().size()]));

        LOG.debug("Dolphin Platform initialized under context \"" + servletContext.getContextPath() + "\"");
        LOG.debug("Dolphin Platform endpoint defined as " + configuration.getDolphinPlatformServletMapping());

        DolphinHttpSessionListener contextCleaner = new DolphinHttpSessionListener();
        contextCleaner.init(configuration);
        servletContext.addListener(contextCleaner);

        java.util.logging.Logger openDolphinLogger = java.util.logging.Logger.getLogger("org.opendolphin");
        openDolphinLogger.setLevel(configuration.getOpenDolphinLogLevel());
    }

    private ContainerManager findManager() {
        final ServiceLoader<ContainerManager> serviceLoader = ServiceLoader.load(ContainerManager.class);
        final Iterator<ContainerManager> serviceIterator = serviceLoader.iterator();
        if (serviceIterator.hasNext()) {
            final ContainerManager containerManager = serviceIterator.next();
            if (serviceIterator.hasNext()) {
                throw new IllegalStateException("More than 1 " + ContainerManager.class + " found!");
            }
            LOG.debug("Container Manager of type {} is used", containerManager.getClass().getSimpleName());
            return containerManager;
        } else {
            throw new IllegalStateException("No " + ContainerManager.class + " found!");
        }
    }

    private void registerDolphinSessionListener(final ContainerManager containerManager, final ClasspathScanner classpathScanner) {
        Assert.requireNonNull(containerManager, "containerManager");
        Assert.requireNonNull(classpathScanner, "classpathScanner");
        final Set<Class<?>> listeners = classpathScanner.getTypesAnnotatedWith(DolphinListener.class);
        for (final Class<?> listenerClass : listeners) {
            try {
                if (DolphinSessionListener.class.isAssignableFrom(listenerClass)) {
                    sessionLifecycleHandler.addSessionDestroyedListener(new Callback<DolphinSession>() {
                        @Override
                        public void call(DolphinSession dolphinSession) {
                            final DolphinSessionListener listener = (DolphinSessionListener) containerManager.createListener(listenerClass);
                            listener.sessionDestroyed(dolphinSession);
                        }
                    });
                    sessionLifecycleHandler.addSessionCreatedListener(new Callback<DolphinSession>() {
                        @Override
                        public void call(DolphinSession dolphinSession) {
                            final DolphinSessionListener listener = (DolphinSessionListener) containerManager.createListener(listenerClass);
                            listener.sessionCreated(dolphinSession);
                        }
                    });
                }
            } catch (Exception e) {
                throw new DolphinPlatformBoostrapException("Error in creating DolphinSessionListener " + listenerClass, e);
            }
        }
    }


    //TODO: The static methods should be refactored...

    public static DolphinSessionProvider getSessionProvider() {
        return sessionProvider;
    }

    public static DolphinSessionLifecycleHandler getSessionLifecycleHandler() {
        return sessionLifecycleHandler;
    }

    public static DolphinEventBus createEventBus(DolphinPlatformConfiguration configuration) {
        Iterator<EventBusProvider> iterator = ServiceLoader.load(EventBusProvider.class).iterator();
        while (iterator.hasNext()) {
            EventBusProvider provider = iterator.next();
            if (configuration.getEventbusType().equals(provider.getType())) {
                return provider.create(configuration);
            }
        }
        throw new IllegalArgumentException("No event bus provider of type " + configuration.getEventbusType() + " found!");
    }

    public static DolphinPlatformConfiguration getConfiguration(ServletContext servletContext) {
        Object attribute = servletContext.getAttribute(CONFIGURATION_ATTRIBUTE_NAME);
        if (attribute != null && attribute instanceof DolphinPlatformConfiguration) {
            return (DolphinPlatformConfiguration) attribute;
        }
        throw new IllegalStateException("Configuration not stored in servlet context!");
    }
}
