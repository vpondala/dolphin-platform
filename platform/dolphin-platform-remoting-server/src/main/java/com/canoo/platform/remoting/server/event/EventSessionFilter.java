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
package com.canoo.platform.remoting.server.event;

import java.io.Serializable;

/**
 * A filter that is used by the {@link DolphinEventBus} to check if a message / event should be send to a specific subscription. The filtering is done by checking the session id of the current client session (see {@link com.canoo.platform.server.client.ClientSession}) of the subscription.
 *
 * @author Hendrik Ebbers
 */
public interface EventSessionFilter extends Serializable {

    /**
     * Returns true if a event should be send to the subscriptions that are part of the client session (see {@link com.canoo.platform.server.client.ClientSession}) that is defined by the given sessionID
     *
     * @param sessionId the client session id
     * @return true if the event shoudl be send to all subsciptions of the client session, otherwise false
     */
    boolean shouldHandleEvent(String sessionId);

}
