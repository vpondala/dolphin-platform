package org.opendolphin.core.server.action;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opendolphin.core.comm.ChangeAttributeMetadataCommand;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DefaultServerDolphin;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerDolphinFactory;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.ArrayList;
import java.util.Collections;

public class StoreAttributeActionTests {

    private DefaultServerDolphin dolphin;

    private ActionRegistry registry;

    @Before
    public void setUp() throws Exception {
        dolphin = ((DefaultServerDolphin) (ServerDolphinFactory.create()));
        dolphin.getModelStore().setCurrentResponse(new ArrayList<Command>());
        registry = new ActionRegistry();
    }

    @Test
    public void testChangeAttributeMetadata() {
        StoreAttributeAction action = new StoreAttributeAction();
        action.setServerModelStore(dolphin.getModelStore());
        action.registerIn(registry);
        ServerAttribute attribute = new ServerAttribute("newAttribute", "");
        dolphin.getModelStore().add(new ServerPresentationModel("model", Collections.singletonList(attribute), dolphin.getModelStore()));
        registry.getActionsFor(ChangeAttributeMetadataCommand.class).get(0).handleCommand(new ChangeAttributeMetadataCommand(attribute.getId(), "value", "newValue"), Collections.emptyList());
        Assert.assertEquals("newValue", attribute.getValue());
    }

}
