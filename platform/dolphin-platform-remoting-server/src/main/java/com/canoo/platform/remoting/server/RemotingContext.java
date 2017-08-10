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
package com.canoo.platform.remoting.server;

import com.canoo.platform.remoting.BeanManager;
import com.canoo.platform.remoting.server.binding.PropertyBinder;
import com.canoo.platform.server.client.ClientSession;
import com.canoo.platform.remoting.server.event.DolphinEventBus;

public interface RemotingContext {

    String getId();

    ClientSessionExecutor createSessionExecutor();

    PropertyBinder getBinder();

    BeanManager getBeanManager();

    DolphinEventBus getEventBus();

    ClientSession getClientSession();
}
