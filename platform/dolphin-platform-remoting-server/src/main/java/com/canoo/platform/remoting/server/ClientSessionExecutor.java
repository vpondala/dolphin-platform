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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * The ClientSessionExecutor can be used to execute tasks later on a specific client session (see {@link com.canoo.platform.server.client.ClientSession}).
 */
public interface ClientSessionExecutor {

    /**
     * Executes the given task later in the given client session
     * @param task the task
     * @return a future that is finished once the task is finished.
     */
    Future<Void> runLaterInClientSession(final Runnable runnable);

    <T> Future<T> callLaterInClientSession(final Callable<T> callable);

}
