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
package org.opendolphin.core.client.comm;

import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleExceptionHandler implements ExceptionHandler {

    private static final Logger LOG = Logger.getLogger(SimpleExceptionHandler.class.getName());

    private final Executor uiExecutor;

    public SimpleExceptionHandler(Executor uiExecutor) {
        this.uiExecutor = uiExecutor;
    }

    @Override
    public void handle(final Throwable e) {
        LOG.log(Level.SEVERE, "onException reached, rethrowing in UI Thread, consider setting AbstractClientConnector.onException", e);
        uiExecutor.execute(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException(e);
            }
        });
    }
}
