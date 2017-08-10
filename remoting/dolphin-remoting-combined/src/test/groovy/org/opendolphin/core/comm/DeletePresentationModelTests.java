package org.opendolphin.core.comm;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeletePresentationModelTests {

    final private class TriggerDeleteCommand extends Command {
    }

    private volatile TestInMemoryConfig context;
    private DefaultServerDolphin serverDolphin;
    private ClientDolphin clientDolphin;

    @Before
    public void setUp() {
        context = new TestInMemoryConfig();
        serverDolphin = ((DefaultServerDolphin) (context.getServerDolphin()));
        clientDolphin = context.getClientDolphin();
    }

    @After
    public void tearDown() {
        try {
            Assert.assertTrue(context.getDone().await(2, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, final Class<T> commandClass, final CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registry.register(commandClass, handler);
            }

        });
    }

    @Test
    public void testCreateAndDeletePresentationModel() {
        // create the pm
        final String modelId = "modelId";
        ClientPresentationModel model = clientDolphin.getModelStore().createModel(modelId, null, new ClientAttribute("someAttribute", "someValue"));
        // sanity check: we have a least the client model store listening to changes of someAttribute
        Assert.assertTrue(model.getAttribute("someAttribute").getPropertyChangeListeners().length > 0);
        // the model is in the client model store
        PresentationModel found = clientDolphin.getModelStore().findPresentationModelById(modelId);
        Assert.assertEquals(model, found);
        // ... and in the server model store after roundtrip
        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById(modelId));
            }

        });
        // when we now delete the pm
        clientDolphin.getModelStore().delete(model);
        // ... it is no longer in the client model store
        Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById(modelId));
        // ... all listeners have been detached from model and all its attributes
        Assert.assertTrue(model.getAttribute("someAttribute").getPropertyChangeListeners().length == 0);

        // the model is also gone from the server model store
        clientDolphin.sync(new Closure<Object>(this, this) {
            public void doCall(Object it) {
                assert !DefaultGroovyMethods.asBoolean(serverDolphin.getModelStore().findPresentationModelById(modelId));
                context.assertionsDone();
            }

            public void doCall() {
                doCall(null);
            }

        });
    }

    @Test
    public void testCreateAndDeletePresentationModelFromServer() {
        // create the pm
        final String modelId = "modelId";
        ClientPresentationModel model = clientDolphin.getModelStore().createModel(modelId, null, new ClientAttribute("someAttribute", "someValue"));
        // the model is in the client model store
        ClientPresentationModel found = clientDolphin.getModelStore().findPresentationModelById(modelId);
        Assert.assertEquals(model, found);
        // ... and in the server model store after roundtrip
        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById(modelId));
            }

        });

        registerAction(serverDolphin, TriggerDeleteCommand.class, new CommandHandler<TriggerDeleteCommand>() {
            @Override
            public void handleCommand(TriggerDeleteCommand command, List<Command> response) {
                ServerModelStore.deleteCommand(response, modelId);
            }

        });
        // when we now delete the pm
        clientDolphin.getClientConnector().send(new TriggerDeleteCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                clientDolphin.sync(new Runnable() {
                    @Override
                    public void run() {
                        // ... it is no longer in the client model store
                        Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById(modelId));
                    }

                });
                clientDolphin.sync(new Runnable() {
                    @Override
                    public void run() {
                        // the model is also gone from the server model store
                        Assert.assertNull(serverDolphin.getModelStore().findPresentationModelById(modelId));
                        // we are done
                        context.assertionsDone();
                    }

                });
            }

        });
    }
}
