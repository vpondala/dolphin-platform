package org.opendolphin.core.server.action;

import groovy.util.GroovyTestCase;
import org.junit.Assert;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.ValueChangedCommand;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.comm.ActionRegistry;

import java.util.ArrayList;

public class DolphinServerActionTests extends GroovyTestCase {

    private DolphinServerAction action;

    @Override
    protected void setUp() throws Exception {
        action = new DolphinServerAction() {
            @Override
            public void registerIn(ActionRegistry registry) {

            }
        };
        action.setDolphinResponse(new ArrayList());
    }

    public void testCreatePresentationModel() {
        action.presentationModel("p1", "person", new DTO());
        Assert.assertEquals(1, action.getDolphinResponse().size());
        Assert.assertEquals(CreatePresentationModelCommand.class, action.getDolphinResponse().get(0).getClass());
        Assert.assertEquals("p1", ((CreatePresentationModelCommand) action.getDolphinResponse().get(0)).getPmId());
        Assert.assertEquals("person", ((CreatePresentationModelCommand) action.getDolphinResponse().get(0)).getPmType());
    }

    public void testChangeValue() {
        action.changeValue(new ServerAttribute("attr", "initial"), "newValue");
        Assert.assertEquals(1, action.getDolphinResponse().size());
        Assert.assertEquals(ValueChangedCommand.class, action.getDolphinResponse().get(0).getClass());
    }
}
