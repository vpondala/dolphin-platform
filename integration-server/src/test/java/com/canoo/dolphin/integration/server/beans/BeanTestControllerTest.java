package com.canoo.dolphin.integration.server.beans;

import com.canoo.dolphin.integration.bean.BeanTestBean;
import com.canoo.dolphin.integration.server.TestConfiguration;
import com.canoo.platform.spring.test.ControllerUnderTest;
import com.canoo.platform.spring.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.canoo.dolphin.integration.bean.BeanTestConstants.BEAN_CONTROLLER_NAME;

@SpringBootTest(classes = TestConfiguration.class)
public class BeanTestControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<BeanTestBean> controller;

    @BeforeMethod
    public void init() {
        controller = createController(BEAN_CONTROLLER_NAME);
    }

    public void destroy() {
        controller.destroy();
    }

    @Test
    public void testInjection() {
        Assert.assertTrue(controller.getModel().getBeanManagerInjected());
        Assert.assertTrue(controller.getModel().getClientSessionInjected());
        Assert.assertTrue(controller.getModel().getDolphinEventBusInjected());
        Assert.assertTrue(controller.getModel().getPropertyBinderInjected());
        Assert.assertTrue(controller.getModel().getRemotingContextInjected());
        destroy();
    }
}
