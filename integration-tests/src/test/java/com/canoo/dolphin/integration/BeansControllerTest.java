package com.canoo.dolphin.integration;

import com.canoo.dolphin.integration.bean.BeanTestBean;
import com.canoo.platform.remoting.client.ClientContext;
import com.canoo.platform.remoting.client.ControllerProxy;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.canoo.dolphin.integration.bean.BeanTestConstants.BEAN_CONTROLLER_NAME;

public class BeansControllerTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if all bean types of Dolphin Platform can be injected in a controller")
    public void testCreateController(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<BeanTestBean> controller = createController(context, BEAN_CONTROLLER_NAME);
            Assert.assertTrue(controller.getModel().getBeanManagerInjected());
            Assert.assertTrue(controller.getModel().getClientSessionInjected());
            Assert.assertTrue(controller.getModel().getDolphinEventBusInjected());
            Assert.assertTrue(controller.getModel().getPropertyBinderInjected());
            Assert.assertTrue(controller.getModel().getRemotingContextInjected());
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Error in test for " + containerType, e);
        }
    }
}
