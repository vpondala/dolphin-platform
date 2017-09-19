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
package com.canoo.platform.spring.test;

import com.canoo.platform.remoting.client.Param;
import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.server.DolphinAction;
import com.canoo.platform.remoting.server.DolphinController;

/**
 * Provides acces to a controller (see {@link DolphinController}) and its model for tests
 * @param <T> type of the model
 */
public interface ControllerUnderTest<T> {

    /**
     * Returns the model of the controller (see {@link DolphinBean})
     * @return the model
     */
    T getModel();

    /**
     * Invokes an action on the controller. See {@link DolphinAction}
     * @param actionName
     * @param params
     */
    void invoke(String actionName, Param... params);

    /**
     * Destroys the controller
     */
    void destroy();
}
