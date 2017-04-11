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
package com.canoo.dolphin.client.util;

import com.canoo.dolphin.BeanManager;
import com.canoo.dolphin.client.impl.ClientBeanBuilderImpl;
import com.canoo.dolphin.client.impl.ClientEventDispatcher;
import com.canoo.dolphin.client.impl.ClientPresentationModelBuilderFactory;
import com.canoo.dolphin.impl.*;
import com.canoo.dolphin.impl.collections.ListMapperImpl;
import com.canoo.dolphin.internal.BeanBuilder;
import com.canoo.dolphin.internal.ClassRepository;
import com.canoo.dolphin.internal.EventDispatcher;
import com.canoo.dolphin.internal.collections.ListMapper;
import core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.DefaultModelSynchronizer;
import org.opendolphin.core.client.ModelSynchronizer;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.util.DirectExecutor;
import org.opendolphin.util.Provider;

import java.util.ArrayList;

public abstract class AbstractDolphinBasedTest {

    public class DolphinTestConfiguration {

        private ClientDolphin clientDolphin;

        private ServerDolphin serverDolphin;

        public DolphinTestConfiguration(ClientDolphin clientDolphin, ServerDolphin serverDolphin) {
            this.clientDolphin = clientDolphin;
            this.serverDolphin = serverDolphin;
        }

        public ClientDolphin getClientDolphin() {
            return clientDolphin;
        }

        public ServerDolphin getServerDolphin() {
            return serverDolphin;
        }
    }

    protected ClientDolphin createClientDolphin(final AbstractClientConnector connector) {
        final ClientDolphin dolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return connector;
            }
        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        dolphin.setClientModelStore(modelStore);
        dolphin.setClientConnector(connector);
        return dolphin;
    }

    protected DolphinTestConfiguration createDolphinTestConfiguration() {
        DefaultInMemoryConfig config = new DefaultInMemoryConfig(DirectExecutor.getInstance());
        config.getServerDolphin().getServerConnector().registerDefaultActions();
        ServerModelStore store = config.getServerDolphin().getModelStore();
        store.setCurrentResponse(new ArrayList<Command>());

        return new DolphinTestConfiguration(config.getClientDolphin(), config.getServerDolphin());
    }

    protected BeanManager createBeanManager(ClientDolphin dolphin) {
        final EventDispatcher dispatcher = new ClientEventDispatcher(dolphin.getModelStore());
        final BeanRepositoryImpl beanRepository = new BeanRepositoryImpl(dolphin.getModelStore(), dispatcher);
        final Converters converters = new Converters(beanRepository);
        final PresentationModelBuilderFactory builderFactory = new ClientPresentationModelBuilderFactory(dolphin.getModelStore());
        final ClassRepository classRepository = new ClassRepositoryImpl(dolphin.getModelStore(), converters, builderFactory);
        final ListMapper listMapper = new ListMapperImpl(dolphin.getModelStore(), classRepository, beanRepository, builderFactory, dispatcher);
        final BeanBuilder beanBuilder = new ClientBeanBuilderImpl(classRepository, beanRepository, listMapper, builderFactory, dispatcher);
        return new BeanManagerImpl(beanRepository, beanBuilder);
    }
}
