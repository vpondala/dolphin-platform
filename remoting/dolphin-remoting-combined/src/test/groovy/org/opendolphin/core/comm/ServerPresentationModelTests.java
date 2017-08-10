package org.opendolphin.core.comm;

import groovy.lang.Closure;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.ModelStoreConfig;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Functional tests for the server-side state changes.
 */
public class ServerPresentationModelTests {

    final private class SetValueCommand extends Command {
    }

    final private class AssertValueCommand extends Command {
    }

    final private class ChangeValueCommand extends Command {
    }

    final private class AttachListenerCommand extends Command {
    }

    final private class ChangeBaseValueCommand extends Command {
    }

    final private class CreateCommand extends Command {
    }

    final private class AssertVisibleCommand extends Command {
    }

    final private class RegisterMSLCommand extends Command {
    }

    final private class RemoveCommand extends Command {
    }

    final private class AssertValueChangeCommand extends Command {
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
            Assert.assertTrue(context.getDone().await(10, TimeUnit.SECONDS));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testServerModelStoreAcceptsConfig() {
        new ServerModelStore(new ModelStoreConfig());
        context.assertionsDone();
    }

    public <T extends Command> void registerAction(ServerDolphin serverDolphin, final Class<T> commandType, final CommandHandler<T> handler) {
        serverDolphin.getServerConnector().register(new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {
                registry.register(commandType, handler);
            }

        });
    }

    @Test
    public void testSecondServerActionCanRelyOnAttributeValueChange() {
        final ClientPresentationModel model = clientDolphin.getModelStore().createModel("PM1", null, new ClientAttribute("att1", null));

        registerAction(serverDolphin, SetValueCommand.class, new CommandHandler<SetValueCommand>() {
            @Override
            public void handleCommand(SetValueCommand command, List<Command> response) {
                serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").setValue(1);
            }

        });

        registerAction(serverDolphin, AssertValueCommand.class, new CommandHandler<AssertValueCommand>() {
            @Override
            public void handleCommand(AssertValueCommand command, List<Command> response) {
                Assert.assertEquals(1, serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").getValue());
            }

        });


        clientDolphin.getClientConnector().send(new SetValueCommand(), null);
        clientDolphin.getClientConnector().send(new AssertValueCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals(1, model.getAttribute("att1").getValue());
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testServerSideValueChangesUseQualifiers() {
        ClientPresentationModel model = clientDolphin.getModelStore().createModel("PM1", null, new ClientAttribute("att1", "base"), new ClientAttribute("att2", "base"));
        model.getAttribute("att1").setQualifier("qualifier");
        model.getAttribute("att2").setQualifier("qualifier");

        registerAction(serverDolphin, ChangeValueCommand.class, new CommandHandler<ChangeValueCommand>() {
            @Override
            public void handleCommand(ChangeValueCommand command, List<Command> response) {
                Attribute at1 = serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1");
                Assert.assertEquals("base", ((ServerAttribute) at1).getValue());
                ((ServerAttribute) at1).setValue("changed");
                Assert.assertEquals("changed", serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att2").getValue());
            }

        });


        clientDolphin.getClientConnector().send(new ChangeValueCommand(), null);

        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testServerSideEventListenerCanChangeSelfValue() {
        final ClientPresentationModel model = clientDolphin.getModelStore().createModel("PM1", null, new ClientAttribute("att1", "base"));


        registerAction(serverDolphin, AttachListenerCommand.class, new CommandHandler<AttachListenerCommand>() {
            @Override
            public void handleCommand(AttachListenerCommand command, List<Command> response) {
                final ServerAttribute at1 = serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1");
                at1.addPropertyChangeListener("value", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        at1.setValue("changed from PCL");
                    }

                });
            }

        });

        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {

            }

        });

        clientDolphin.getClientConnector().send(new AttachListenerCommand(), null);

        clientDolphin.sync(new Closure<Object>(this, this) {
            public void doCall(Object it) {
                model.getAttribute("att1").setValue("changed");
                clientDolphin.sync(new Closure<Object>(ServerPresentationModelTests.this, ServerPresentationModelTests.this) {
                    public void doCall(Object it) {
                        Assert.assertEquals("changed from PCL", model.getAttribute("att1").getValue());
                        context.assertionsDone();
                    }

                    public void doCall() {
                        doCall(null);
                    }

                });
            }

            public void doCall() {
                doCall(null);
            }

        });
    }

    @Test
    public void testSecondServerActionCanRelyOnPmCreate() {

        final AtomicReference<PresentationModel> pmWithNullId = new AtomicReference<>();


        registerAction(serverDolphin, CreateCommand.class, new CommandHandler<CreateCommand>() {
            @Override
            public void handleCommand(CreateCommand command, List<Command> response) {
                DTO dto = new DTO(new Slot("att1", 1));
                PresentationModel pm = serverDolphin.getModelStore().presentationModel("PM1", null, dto);
                pmWithNullId.set(serverDolphin.getModelStore().presentationModel(null, "pmType", dto));
                Assert.assertNotNull(pm);
                Assert.assertNotNull(pmWithNullId.get());
                Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById("PM1"));
                Assert.assertEquals(pmWithNullId.get(), serverDolphin.getModelStore().findAllPresentationModelsByType("pmType").get(0));
            }

        });
        registerAction(serverDolphin, AssertVisibleCommand.class, new CommandHandler<AssertVisibleCommand>() {
            @Override
            public void handleCommand(AssertVisibleCommand command, List<Command> response) {
                Assert.assertNotNull(serverDolphin.getModelStore().findPresentationModelById("PM1"));
                Assert.assertEquals(pmWithNullId.get(), serverDolphin.getModelStore().findAllPresentationModelsByType("pmType").get(0));
            }

        });

        clientDolphin.getClientConnector().send(new CreateCommand(), null);
        clientDolphin.getClientConnector().send(new AssertVisibleCommand(), null);

        clientDolphin.sync(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotNull(clientDolphin.getModelStore().findPresentationModelById("PM1"));
                Assert.assertEquals(1, clientDolphin.getModelStore().findAllPresentationModelsByType("pmType").size());
                System.out.println(clientDolphin.getModelStore().findAllPresentationModelsByType("pmType").get(0).getId());
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testServerCreatedAttributeChangesValueOnTheClientSide() {

        final AtomicBoolean pclReached = new AtomicBoolean(false);

        registerAction(serverDolphin, CreateCommand.class, new CommandHandler<CreateCommand>() {
            @Override
            public void handleCommand(CreateCommand command, List<Command> response) {
                DTO dto = new DTO(new Slot("att1", 1));
                serverDolphin.getModelStore().presentationModel("PM1", null, dto);
                serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").addPropertyChangeListener("value", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        pclReached.set(true);
                    }

                });
            }

        });

        registerAction(serverDolphin, AssertValueChangeCommand.class, new CommandHandler<AssertValueChangeCommand>() {
            @Override
            public void handleCommand(AssertValueChangeCommand command, List<Command> response) {
                Assert.assertTrue(pclReached.get());
                Assert.assertEquals(2, serverDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").getValue());
                context.assertionsDone();
            }

        });


        clientDolphin.getClientConnector().send(new CreateCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals(1, clientDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").getValue());
                clientDolphin.getModelStore().findPresentationModelById("PM1").getAttribute("att1").setValue(2);
                clientDolphin.getClientConnector().send(new AssertValueChangeCommand(), null);
            }

        });

    }

    @Test
    public void testServerSidePmRemoval() {

        clientDolphin.getModelStore().createModel("client-side-with-id", null, new ClientAttribute("attr1", 1));

        registerAction(serverDolphin, RemoveCommand.class, new CommandHandler<RemoveCommand>() {
            @Override
            public void handleCommand(RemoveCommand command, List<Command> response) {
                PresentationModel pm = serverDolphin.getModelStore().findPresentationModelById("client-side-with-id");
                Assert.assertNotNull(pm);
                serverDolphin.getModelStore().remove((ServerPresentationModel) pm);
                Assert.assertNull(serverDolphin.getModelStore().findPresentationModelById("client-side-with-id"));
                // immediately removed on server
            }

        });

        Assert.assertNotNull(clientDolphin.getModelStore().findPresentationModelById("client-side-with-id"));


        clientDolphin.getClientConnector().send(new RemoveCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertNull(clientDolphin.getModelStore().findPresentationModelById("client-side-with-id"));
                // removed from client before callback
                context.assertionsDone();
            }

        });
    }

    @Test
    public void testServerSideBaseValueChange() {
        final ClientPresentationModel source = clientDolphin.getModelStore().createModel("source", null, new ClientAttribute("attr1", "sourceValue"));

        registerAction(serverDolphin, ChangeBaseValueCommand.class, new CommandHandler<ChangeBaseValueCommand>() {
            @Override
            public void handleCommand(ChangeBaseValueCommand command, List<Command> response) {
                Attribute attribute = serverDolphin.getModelStore().findPresentationModelById("source").getAttribute("attr1");
            }

        });

        clientDolphin.getClientConnector().send(new ChangeBaseValueCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals("sourceValue", source.getAttribute("attr1").getValue());
                context.assertionsDone();
            }

        });
    }

    public void testServerSideQualifierChange() {
        final ClientPresentationModel source = clientDolphin.getModelStore().createModel("source", null, new ClientAttribute("attr1", "sourceValue"));
        source.getAttribute("attr1").setQualifier("qualifier");

        registerAction(serverDolphin, ChangeBaseValueCommand.class, new CommandHandler<ChangeBaseValueCommand>() {
            @Override
            public void handleCommand(ChangeBaseValueCommand command, List<Command> response) {
                Attribute attribute = serverDolphin.getModelStore().findPresentationModelById("source").getAttribute("attr1");
                ((ServerAttribute) attribute).setQualifier("changed");
                Assert.assertEquals("changed", ((ServerAttribute) attribute).getQualifier());
            }

        });


        clientDolphin.getClientConnector().send(new ChangeBaseValueCommand(), new OnFinishedHandler() {
            @Override
            public void onFinished() {
                Assert.assertEquals("sourceValue", source.getAttribute("attr1").getValue());
                Assert.assertEquals("changed", source.getAttribute("attr1").getQualifier());
                context.assertionsDone();
            }

        });
    }

}
