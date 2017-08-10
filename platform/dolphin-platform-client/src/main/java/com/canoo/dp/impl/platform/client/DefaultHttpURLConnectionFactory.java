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
package com.canoo.dp.impl.platform.client;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.HttpURLConnectionFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DefaultHttpURLConnectionFactory implements HttpURLConnectionFactory {

    @Override
    public HttpURLConnection create(final URL url) throws IOException {
        Assert.requireNonNull(url, "url");
        final URLConnection connection = url.openConnection();
        if(connection instanceof HttpURLConnection) {
            return (HttpURLConnection) connection;
        }
        throw new IOException("URL do not provide a HttpURLConnection!");
    }
}
