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
package com.canoo.dolphin.test.impl;

import com.canoo.dolphin.server.config.DolphinPlatformConfiguration;
import com.canoo.dolphin.server.container.ContainerManager;
import com.canoo.dolphin.server.context.DolphinContext;
import com.canoo.dolphin.server.context.DolphinContextUtils;
import com.canoo.dolphin.server.context.DolphinSessionProvider;
import com.canoo.dolphin.server.context.OpenDolphinFactory;
import com.canoo.dolphin.server.controller.ControllerRepository;
import com.canoo.dolphin.util.Callback;
import org.opendolphin.core.comm.Command;

import java.util.List;

public class DolphinTestContext extends DolphinContext {

    public DolphinTestContext(DolphinPlatformConfiguration configuration, DolphinSessionProvider dolphinSessionProvider, ContainerManager containerManager, ControllerRepository controllerRepository, OpenDolphinFactory openDolphinFactory) {
        super(configuration, dolphinSessionProvider, containerManager, controllerRepository,openDolphinFactory, createEmptyCallback(), createEmptyCallback());
        DolphinContextUtils.setContextForCurrentThread(this);
    }

    private static Callback<DolphinContext> createEmptyCallback() {
        return new Callback<DolphinContext>() {
            @Override
            public void call(DolphinContext context) {

            }
        };
    }

    @Override
    public List<Command> handle(List<Command> commands) {
        DolphinContextUtils.setContextForCurrentThread(this);
        return super.handle(commands);
    }
}
