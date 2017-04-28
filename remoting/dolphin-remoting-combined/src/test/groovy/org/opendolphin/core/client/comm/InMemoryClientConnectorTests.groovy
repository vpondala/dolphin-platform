package org.opendolphin.core.client.comm;

import core.client.comm.InMemoryClientConnector;
import groovy.util.GroovyTestCase;
import org.junit.Assert;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.DefaultModelSynchronizer;
import org.opendolphin.core.client.ModelSynchronizer;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.EmptyNotification;
import org.opendolphin.core.server.ServerConnector;
import org.opendolphin.util.DirectExecutor;
import org.opendolphin.util.Provider;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InMemoryClientConnectorTests extends GroovyTestCase {
    public void testCallConnector_NoServerConnectorWired() {

        //given:
        ServerConnector serverConnector = new ServerConnector() {
            @Override
            public List<Command> receive(Command command) {
                return Collections.emptyList();
            }

        };

        final ClientDolphin clientDolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return clientDolphin.getClientConnector();
            }

        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        InMemoryClientConnector connector = new InMemoryClientConnector(modelStore, serverConnector, new CommandBatcher(), DirectExecutor.getInstance());

        clientDolphin.setClientConnector(connector);
        clientDolphin.setClientModelStore(modelStore);

        //when:
        List<Command> ret = connector.transmit(Collections.singletonList(new EmptyNotification()));

        //then:
        Assert.assertEquals(Collections.emptyList(), ret);
    }

    public void testCallConnector_ServerWired() {

        //given:
        final AtomicBoolean serverCalled = new AtomicBoolean(false);
        ServerConnector serverConnector = new ServerConnector() {
            @Override
            public List<Command> receive(Command command) {
                serverCalled.set(true);
                return Collections.emptyList();
            }

        };

        final ClientDolphin clientDolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return clientDolphin.getClientConnector();
            }

        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        AbstractClientConnector connector = new InMemoryClientConnector(modelStore, serverConnector, new CommandBatcher(), DirectExecutor.getInstance());

        clientDolphin.setClientConnector(connector);
        clientDolphin.setClientModelStore(modelStore);

        //when:
        ((InMemoryClientConnector) connector).transmit(Collections.singletonList(new EmptyNotification()));

        //then:
        Assert.assertTrue(serverCalled.get());
    }

    public void testCallConnector_ServerWiredWithSleep() {

        //given:
        final AtomicBoolean serverCalled = new AtomicBoolean(false);
        ServerConnector serverConnector = new ServerConnector() {
            @Override
            public List<Command> receive(Command command) {
                serverCalled.set(true);
                return Collections.emptyList();
            }

        };
        final ClientDolphin clientDolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            public AbstractClientConnector get() {
                return clientDolphin.getClientConnector();
            }

        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        AbstractClientConnector connector = new InMemoryClientConnector(modelStore, serverConnector, new CommandBatcher(), DirectExecutor.getInstance());
        clientDolphin.setClientConnector(connector);
        clientDolphin.setClientModelStore(modelStore);
        ((InMemoryClientConnector) connector).setSleepMillis(10);

        //when:
        ((InMemoryClientConnector) connector).transmit(Collections.singletonList(new EmptyNotification()));

        //then:
        Assert.assertTrue(serverCalled.get());
    }

}
