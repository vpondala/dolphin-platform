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
package com.canoo.dp.impl.server.event;

import com.canoo.platform.server.spi.PlatformConfiguration;

import java.io.Serializable;

public class HazelcastConfig implements Serializable {

    private final PlatformConfiguration configuration;

    public HazelcastConfig(PlatformConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getServerName() {
        return configuration.getProperty(DistributedEventBusConfigProvider.HAZELCAST_SERVER_NAME);
    }

    public String getServerPort() {
        return configuration.getProperty(DistributedEventBusConfigProvider.HAZELCAST_SERVER_PORT);
    }

    public String getGroupName() {
        return configuration.getProperty(DistributedEventBusConfigProvider.HAZELCAST_GROUP_NAME);
    }

}
