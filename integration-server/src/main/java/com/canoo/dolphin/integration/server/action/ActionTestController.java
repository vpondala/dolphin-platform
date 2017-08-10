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

import com.canoo.dolphin.integration.action.ActionTestBean;
import com.canoo.platform.remoting.server.DolphinAction;
import com.canoo.platform.remoting.server.DolphinController;
import com.canoo.platform.remoting.server.DolphinModel;
import com.canoo.platform.remoting.server.Param;

import static com.canoo.dolphin.integration.action.ActionTestConstants.*;

@DolphinController(ACTION_CONTROLLER_NAME)
public class ActionTestController {

    @DolphinModel
    private ActionTestBean model;

    @DolphinAction(RESET_MODEL_ACTION)
    public void resetModel() {
        model.setBooleanValue(null);
        model.setStringValue(null);
    }

    @DolphinAction(PUBLIC_ACTION)
    public void publicAction() {
        model.setBooleanValue(true);
    }

    @DolphinAction(PRIVATE_ACTION)
    private void privateAction() {
        model.setBooleanValue(true);
    }

    @DolphinAction(PUBLIC_WITH_BOOLEAN_PARAM_ACTION)
    private void withStringParam(@Param(PARAM_NAME) Boolean value) {
        model.setBooleanValue(value);
    }

    @DolphinAction(PRIVATE_WITH_STRING_PARAM_ACTION)
    public void withStringParam(@Param(PARAM_NAME) String value) {
        model.setBooleanValue(true);
        model.setStringValue(value);
    }

    @DolphinAction(PRIVATE_WITH_SEVERAL_PARAMS_ACTION)
    private void withSeveralParams(@Param(PARAM_NAME_1) String value1, @Param(PARAM_NAME_2) String value2, @Param(PARAM_NAME_3) int value3) {
        model.setBooleanValue(true);
        model.setStringValue(value1 + value2 + value3);
    }

    @DolphinAction(WITH_EXCEPTION_ACTION)
    private void withException() {
        throw new RuntimeException("huhu");
    }
}
