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
package com.canoo.dolphin.integration.server.action;

import com.canoo.platform.remoting.client.Param;
import com.canoo.dolphin.integration.action.ActionTestBean;
import com.canoo.dolphin.integration.server.TestConfiguration;
import com.canoo.platform.spring.test.ControllerTestException;
import com.canoo.platform.spring.test.ControllerUnderTest;
import com.canoo.platform.spring.test.SpringTestNGControllerTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.canoo.dolphin.integration.action.ActionTestConstants.*;

@SpringBootTest(classes = TestConfiguration.class)
public class ActionTestControllerTest extends SpringTestNGControllerTest {

    private ControllerUnderTest<ActionTestBean> controller;

    @BeforeMethod
    public void init() {
        controller = createController(ACTION_CONTROLLER_NAME);
    }

    @AfterMethod
    public void destroy() {
        controller.destroy();
    }

    @Test
    public void callSimpleMethod() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        controller.invoke(PUBLIC_ACTION);
        Assert.assertTrue(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPrivateMethod() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        controller.invoke(PRIVATE_ACTION);
        Assert.assertTrue(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPrivateMethodWithStringParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        String value = "Hello Dolphin Platform!";
        controller.invoke(PRIVATE_WITH_STRING_PARAM_ACTION, new Param(PARAM_NAME, value));
        Assert.assertTrue(controller.getModel().getBooleanValue());
        Assert.assertEquals(controller.getModel().getStringValue(), value);
    }

    @Test
    public void callPrivateMethodWithNullParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        controller.invoke(PRIVATE_WITH_STRING_PARAM_ACTION, new Param(PARAM_NAME, null));
        Assert.assertTrue(controller.getModel().getBooleanValue());
        Assert.assertNull(controller.getModel().getStringValue());
    }

    @Test
    public void callPublicMethodWithBooleanParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        String value = "Hello Dolphin Platform!";
        controller.invoke(PUBLIC_WITH_BOOLEAN_PARAM_ACTION, new Param(PARAM_NAME, true));
        Assert.assertTrue(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPublicMethodWithNullParam() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        String value = "Hello Dolphin Platform!";
        controller.invoke(PUBLIC_WITH_BOOLEAN_PARAM_ACTION, new Param(PARAM_NAME, null));
        Assert.assertNull(controller.getModel().getBooleanValue());
    }

    @Test
    public void callPrivateMethodWithSeveralParams() {
        Assert.assertNull(controller.getModel().getBooleanValue());
        String value1 = "Hello Dolphin Platform!";
        String value2 = "I want to test you!";
        int value3 = 356;
        controller.invoke(PRIVATE_WITH_SEVERAL_PARAMS_ACTION, new Param(PARAM_NAME_1, value1), new Param(PARAM_NAME_2, value2), new Param(PARAM_NAME_3, value3));
        Assert.assertTrue(controller.getModel().getBooleanValue());
        Assert.assertEquals(controller.getModel().getStringValue(), value1 + value2 + value3);
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void callMethodWithException() {
        controller.invoke(WITH_EXCEPTION_ACTION);
    }

    @Test(expectedExceptions = ControllerTestException.class)
    public void callMethodWithWrongParamaters() {
        controller.invoke(PRIVATE_WITH_STRING_PARAM_ACTION);
    }
}
