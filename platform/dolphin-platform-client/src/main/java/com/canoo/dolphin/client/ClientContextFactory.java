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
package com.canoo.dolphin.client;

import com.canoo.dolphin.client.impl.ClientContextImpl;
import com.canoo.dolphin.client.impl.DolphinPlatformHttpClientConnector;
import com.canoo.dolphin.impl.codec.OptimizedJsonCodec;
import com.canoo.dolphin.util.Assert;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.util.Function;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory to create a {@link ClientContext}. Normally you will create a {@link ClientContext} at the bootstrap of your
 * client by using the {@link #connect(ClientConfiguration)} method and use this context as a singleton in your client.
 * The {@link ClientContext} defines the connection between the client and the Dolphin Platform server endpoint.
 */
public class ClientContextFactory {

    private ClientContextFactory() {
    }

    /**
     * Create a {@link ClientContext} based on the given configuration. This method doesn't block and returns a
     * {@link CompletableFuture} to receive its result. If the {@link ClientContext} can't be created the
     * {@link CompletableFuture#get()} will throw a {@link ClientInitializationException}.
     *
     * @param clientConfiguration the configuration
     * @return the future
     */
    public static CompletableFuture<ClientContext> connect(final ClientConfiguration clientConfiguration) {
        Assert.requireNonNull(clientConfiguration, "clientConfiguration");
        final CompletableFuture<ClientContext> result = new CompletableFuture<>();

        Level openDolphinLogLevel = clientConfiguration.getDolphinLogLevel();
        Logger openDolphinLogger = Logger.getLogger("org.opendolphin");
        openDolphinLogger.setLevel(openDolphinLogLevel);

        clientConfiguration.getBackgroundExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    final ClientContext clientContext = new ClientContextImpl(clientConfiguration, new Function<ClientModelStore, AbstractClientConnector>() {
                        @Override
                        public AbstractClientConnector call(final ClientModelStore clientModelStore) {
                            return new DolphinPlatformHttpClientConnector(clientConfiguration, clientModelStore, new OptimizedJsonCodec(), clientConfiguration.getRemotingExceptionHandler());
                        }
                    });
                    clientContext.connect().get(clientConfiguration.getConnectionTimeout(), TimeUnit.MILLISECONDS);

                    clientConfiguration.getUiExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            result.complete(clientContext);
                        }
                    });
                } catch (final Exception exception) {
                    clientConfiguration.getUiExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            result.obtrudeException(new ClientInitializationException("Can not connect to server!", exception));
                        }
                    });
                }
            }
        });
        return result;
    }

}
