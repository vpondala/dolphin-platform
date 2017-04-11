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
package com.canoo.dolphin.integration;

import com.canoo.dolphin.client.ClientContext;
import com.canoo.dolphin.client.ControllerProxy;
import com.canoo.dolphin.client.Param;
import com.canoo.dolphin.integration.action.ActionTestBean;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.canoo.dolphin.integration.action.ActionTestConstants.*;

public class ActionControllerTest extends AbstractIntegrationTest {

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Test if controller and model can be created")
    public void testCreateController(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ActionTestBean> controller = createController(context, ACTION_CONTROLLER_NAME);
            Assert.assertNotNull(controller);
            Assert.assertNotNull(controller.getModel());
            Assert.assertEquals(controller.getModel().getClass(), ActionTestBean.class);
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if an public action method can be called")
    public void testCallPublicMethod(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ActionTestBean> controller = createController(context, ACTION_CONTROLLER_NAME);
            invoke(controller, PUBLIC_ACTION, containerType);
            Assert.assertTrue(controller.getModel().getBooleanValue());
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if an private action method can be called")
    public void testCallPrivateMethod(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ActionTestBean> controller = createController(context, ACTION_CONTROLLER_NAME);
            invoke(controller, PRIVATE_ACTION, containerType);
            Assert.assertTrue(controller.getModel().getBooleanValue());
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if an private action method can be called")
    public void testCallWithParam(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ActionTestBean> controller = createController(context, ACTION_CONTROLLER_NAME);
            invoke(controller, WITH_STRING_PARAM_ACTION, containerType, new Param(PARAM_NAME, "Yeah!"));
            Assert.assertTrue(controller.getModel().getBooleanValue());
            Assert.assertEquals(controller.getModel().getStringValue(), "Yeah!");
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }

    @Test(dataProvider = ENDPOINTS_DATAPROVIDER, description = "Tests if an private action method can be called")
    public void testCallWithParams(String containerType, String endpoint) {
        try {
            ClientContext context = connect(endpoint);
            ControllerProxy<ActionTestBean> controller = createController(context, ACTION_CONTROLLER_NAME);

            String value1 = "Hello Dolphin Platform!";
            String value2 = "I want to test you!";
            int value3 = 356;

            invoke(controller, WITH_SEVERAL_PARAMS_ACTION, containerType, new Param(PARAM_NAME_1, value1), new Param(PARAM_NAME_2, value2), new Param(PARAM_NAME_3, value3));
            Assert.assertTrue(controller.getModel().getBooleanValue());
            Assert.assertEquals(controller.getModel().getStringValue(), value1 + value2 + value3);
            destroy(controller, endpoint);
            disconnect(context, endpoint);
        } catch (Exception e) {
            Assert.fail("Can not create controller for " + containerType, e);
        }
    }
}
