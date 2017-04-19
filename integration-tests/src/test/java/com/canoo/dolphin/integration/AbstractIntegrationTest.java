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
package com.canoo.dolphin.integration;

import com.canoo.dolphin.client.*;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public class AbstractIntegrationTest {

    public final static String ENDPOINTS_DATAPROVIDER = "endpoints";

    protected void waitUntilServerIsUp(String host, long time, TimeUnit timeUnit) throws TimeoutException {
        long startTime = System.currentTimeMillis();
        long waitMillis = timeUnit.toMillis(time);
        boolean connected = false;
        while (!connected) {
            if(System.currentTimeMillis() > startTime + waitMillis) {
                throw new TimeoutException("Server " + host + " is still down after " + waitMillis + " ms");
            }
            try {
                URL healthUrl = new URL(host + "/rest/health");
                URLConnection connection = healthUrl.openConnection();
                if(connection instanceof HttpURLConnection) {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                    httpURLConnection.connect();
                    if(httpURLConnection.getResponseCode() == 200) {
                        connected = true;
                    }
                } else {
                    throw new IOException("URL " + healthUrl + " do not provide a HttpURLConnection!");
                }
            } catch (Exception e) {
                // do nothing since server is not up at the moment...
            }
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected <T> ControllerProxy<T> createController(ClientContext clientContext, String controllerName) {
        try {
            return (ControllerProxy<T>) clientContext.createController(controllerName).get(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not create controller " + controllerName, e);
        }
    }

    protected ClientContext connect(String endpoint) {
        try {
            waitUntilServerIsUp(endpoint, 5, TimeUnit.MINUTES);
            ClientConfiguration configuration = new ClientConfiguration(new URL(endpoint + "/dolphin"), r -> r.run());
            configuration.setDolphinLogLevel(Level.FINE);
            configuration.setConnectionTimeout(10_000L);
            return ClientContextFactory.connect(configuration).get(configuration.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Can not create client context for endpoint " + endpoint, e);
        }
    }

    protected void invoke(ControllerProxy<?> controllerProxy, String actionName, String containerType, Param... params) {
        try {
            controllerProxy.invoke(actionName, params).get(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not handle action " + actionName + " for containerType " + containerType, e);
        }
    }

    protected void destroy(ControllerProxy<?> controllerProxy, String endpoint) {
        try {
            controllerProxy.destroy().get(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not destroy controller for endpoint " + endpoint, e);
        }
    }

    protected void disconnect(ClientContext clientContext, String endpoint) {
        try {
            clientContext.disconnect().get(2, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException("Can not disconnect client context for endpoint " + endpoint, e);
        }
    }

    protected void sleep(long time, TimeUnit timeUnit) {
        try {
            Thread.sleep(timeUnit.toMillis(time));
        } catch (Exception e) {
            throw new RuntimeException("Can not sleep :(", e);
        }
    }

    @DataProvider(name = ENDPOINTS_DATAPROVIDER, parallel = false)
    public Object[][] getEndpoints() {
        return new String[][]{{"Payara", "http://localhost:8081/integration-tests"},
                {"TomEE", "http://localhost:8082/integration-tests"},
                {"Wildfly", "http://localhost:8083/integration-tests"}//,
                //{"Spring-Boot", "http://localhost:8084/integration-tests"}
        };
    }
}
