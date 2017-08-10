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
package com.canoo.dp.impl.remoting.server.spring;

import com.canoo.dp.impl.remoting.BeanManagerImpl;
import com.canoo.platform.remoting.BeanManager;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.dp.impl.server.context.DolphinContext;
import com.canoo.dp.impl.server.context.DolphinContextProvider;
import com.canoo.dp.impl.server.context.RemotingContextImpl;
import com.canoo.dp.impl.server.event.LazyEventBusInvocationHandler;
import com.canoo.platform.remoting.server.RemotingContext;
import com.canoo.platform.remoting.server.binding.PropertyBinder;
import com.canoo.platform.remoting.server.event.DolphinEventBus;
import com.canoo.platform.server.spring.ClientScope;
import com.canoo.platform.server.spring.SingletonScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

/**
 * Provides all Dolphin Platform Beans and Scopes for CDI
 */
@Configuration
public class RemotingSpringBeanFactory {

    @Bean(name = "remotingContext")
    @ClientScope
    protected RemotingContext createRemotingContext(DolphinEventBus eventBus) {
        Assert.requireNonNull(eventBus, "eventBus");

        final DolphinContextProvider contextProvider = PlatformBootstrap.getServerCoreComponents().getInstance(DolphinContextProvider.class);
        Assert.requireNonNull(contextProvider, "contextProvider");

        final DolphinContext context = contextProvider.getCurrentDolphinContext();
        Assert.requireNonNull(context, "context");

        return new RemotingContextImpl(context, eventBus);
    }

    /**
     * Method to create a spring managed {@link BeanManagerImpl} instance in client scope.
     *
     * @return the instance
     */
    @Bean(name = "beanManager")
    @ClientScope
    protected BeanManager createManager(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBeanManager();
    }

    /**
     * Method to create a spring managed {@link DolphinEventBus} instance in singleton scope.
     *
     * @return the instance
     */
    @Bean(name = "dolphinEventBus")
    @SingletonScope
    protected DolphinEventBus createEventBus() {
        return (DolphinEventBus) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{DolphinEventBus.class}, new LazyEventBusInvocationHandler());
    }

    @Bean(name = "propertyBinder")
    @ClientScope
    protected PropertyBinder createPropertyBinder(RemotingContext remotingContext) {
        Assert.requireNonNull(remotingContext, "remotingContext");
        return remotingContext.getBinder();
    }
}
