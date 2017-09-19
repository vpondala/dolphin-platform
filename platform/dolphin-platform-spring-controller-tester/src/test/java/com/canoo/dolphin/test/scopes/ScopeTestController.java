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
package com.canoo.dolphin.test.scopes;

import com.canoo.platform.remoting.server.DolphinController;
import com.canoo.platform.remoting.server.DolphinModel;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@DolphinController("ScopeTestController")
public class ScopeTestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private SingletonService singletonService;

    @DolphinModel
    private ScopeModel model;

    @PostConstruct
    public void init() {
        model.requestServiceIdProperty().set(requestService.getId());
        model.clientServiceIdProperty().set(clientService.getId());
        model.sessionServiceIdProperty().set(sessionService.getId());
        model.singletonServiceIdProperty().set(singletonService.getId());
    }
}
