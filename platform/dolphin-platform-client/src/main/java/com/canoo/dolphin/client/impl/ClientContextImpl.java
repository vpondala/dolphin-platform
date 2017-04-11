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
package com.canoo.dolphin.client.impl;

import com.canoo.dolphin.client.*;
import com.canoo.dolphin.impl.BeanRepositoryImpl;
import com.canoo.dolphin.impl.ClassRepositoryImpl;
import com.canoo.dolphin.impl.Converters;
import com.canoo.dolphin.impl.PresentationModelBuilderFactory;
import com.canoo.dolphin.impl.commands.CreateContextCommand;
import com.canoo.dolphin.impl.commands.DestroyContextCommand;
import com.canoo.dolphin.internal.BeanRepository;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.DefaultModelSynchronizer;
import org.opendolphin.core.client.ModelSynchronizer;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.util.DolphinRemotingException;
import org.opendolphin.util.Function;
import org.opendolphin.util.Provider;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class ClientContextImpl implements ClientContext {

    private final ClientConfiguration clientConfiguration;

    private final Function<ClientModelStore, AbstractClientConnector> connectorProvider;

    private AbstractClientConnector clientConnector;

    private ClientModelStore modelStore;

    @Deprecated
    private  ClientBeanManagerImpl clientBeanManager;

    private ControllerProxyFactory controllerProxyFactory;

    private DolphinCommandHandler dolphinCommandHandler;

    public ClientContextImpl(ClientConfiguration clientConfiguration, final Function<ClientModelStore, AbstractClientConnector> connectorProvider) {
        this.clientConfiguration = Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        this.connectorProvider = Assert.requireNonNull(connectorProvider, "connectorProvider");

    }

    @Override
    public synchronized <T> CompletableFuture<ControllerProxy<T>> createController(String name) {
        Assert.requireNonBlank(name, "name");

        return controllerProxyFactory.<T>create(name).handle(new BiFunction<ControllerProxy<T>, Throwable, ControllerProxy<T>>() {
            @Override
            public ControllerProxy<T> apply(ControllerProxy<T> controllerProxy, Throwable throwable) {
                if (throwable != null) {
                    throw new ControllerInitalizationException(throwable);
                }
                return controllerProxy;
            }
        });
    }

    @Override
    public synchronized ClientBeanManager getBeanManager() {
        return clientBeanManager;
    }

    @Override
    public synchronized CompletableFuture<Void> disconnect() {
        final CompletableFuture<Void> result = new CompletableFuture<>();

        clientConfiguration.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                dolphinCommandHandler.invokeDolphinCommand(new DestroyContextCommand()).handle(new BiFunction<Void, Throwable, Object>() {
                    @Override
                    public Object apply(Void aVoid, Throwable throwable) {
                        clientConnector.disconnect();
                        if (throwable != null) {
                            result.completeExceptionally(new DolphinRemotingException("Can't disconnect", throwable));
                        } else {
                            result.complete(null);
                        }
                        return null;
                    }
                });
            }
        });
        return result;
    }

    @Override
    public CompletableFuture<Void> connect() {
        final ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return clientConnector;
            }
        });

        this.modelStore = new ClientModelStore(defaultModelSynchronizer);
        this.clientConnector = connectorProvider.call(modelStore);

        final EventDispatcher dispatcher = new ClientEventDispatcher(modelStore);
        final BeanRepository beanRepository = new BeanRepositoryImpl(modelStore, dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(modelStore);
        final ClassRepository classRepository = new ClassRepositoryImpl(modelStore, converters, builderFactory);

        this.dolphinCommandHandler = new DolphinCommandHandler(clientConnector);
        this.controllerProxyFactory = new ControllerProxyFactoryImpl(dolphinCommandHandler, clientConnector, modelStore, beanRepository, dispatcher, converters);
        this.clientBeanManager = new ClientBeanManagerImpl(beanRepository, classRepository, modelStore, builderFactory, dispatcher);

        final CompletableFuture<Void> result = new CompletableFuture<>();
        clientConnector.connect();

        clientConfiguration.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                dolphinCommandHandler.invokeDolphinCommand(new CreateContextCommand()).handle(new BiFunction<Void, Throwable, Void>() {
                    @Override
                    public Void apply(Void aVoid, Throwable throwable) {
                        if (throwable != null) {
                            result.completeExceptionally(new ClientInitializationException("Can't call init action!", throwable));
                        } else {
                        }
                        result.complete(null);
                        return null;
                    }
                });
            }
        });
        return result;
    }
}
