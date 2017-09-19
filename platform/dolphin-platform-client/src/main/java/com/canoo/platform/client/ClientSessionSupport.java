package com.canoo.platform.client;

import com.canoo.dp.impl.platform.client.ClientSessionStore;
import com.canoo.dp.impl.platform.client.ClientSessionSupportingURLConnectionRequestHandler;
import com.canoo.dp.impl.platform.client.ClientSessionSupportingURLConnectionResponseHandler;
import com.canoo.dp.impl.platform.client.DefaultHttpURLConnectionFactory;
import com.canoo.dp.impl.platform.client.SimpleUrlToAppDomainConverter;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.core.functional.Callback;
import com.canoo.platform.core.functional.Function;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientSessionSupport {

    private final ClientSessionStore clientSessionStore;

    private final HttpURLConnectionFactory httpURLConnectionFactory;

    private final HttpURLConnectionHandler requestHandler;

    private final HttpURLConnectionHandler responseHandler;

    public ClientSessionSupport() {
        this(new DefaultHttpURLConnectionFactory(), new SimpleUrlToAppDomainConverter());
    }

    public ClientSessionSupport(final HttpURLConnectionFactory httpURLConnectionFactory) {
        this(httpURLConnectionFactory, new SimpleUrlToAppDomainConverter());
    }

    public ClientSessionSupport(final UrlToAppDomainConverter converter) {
        this(new DefaultHttpURLConnectionFactory(), converter);
    }

    public ClientSessionSupport(final HttpURLConnectionFactory httpURLConnectionFactory, final UrlToAppDomainConverter converter) {
        this.httpURLConnectionFactory = Assert.requireNonNull(httpURLConnectionFactory, "httpURLConnectionFactory");
        this.clientSessionStore = new ClientSessionStore(converter);
        this.requestHandler = new ClientSessionSupportingURLConnectionRequestHandler(clientSessionStore);
        this.responseHandler = new ClientSessionSupportingURLConnectionResponseHandler(clientSessionStore);
    }

    public synchronized HttpURLConnection createConnectionForRequest(final URL url) throws IOException {
        final HttpURLConnection connection = httpURLConnectionFactory.create(url);
        requestHandler.handle(connection);
        return connection;
    }

    public synchronized void handleResponse(final HttpURLConnection response) {
        responseHandler.handle(response);
    }

    public synchronized void resetSession(final URL url) {
        clientSessionStore.resetSession(url);
    }

    public String getClientIdFor(final URL url) {
        return clientSessionStore.getClientIdentifierForUrl(url);
    }

    public void doRequest(final URL url, final Callback<HttpURLConnection> callback) throws IOException {
        Assert.requireNonNull(callback, "callback");

        doRequest(url, new Function<HttpURLConnection, Void>() {
            @Override
            public Void call(HttpURLConnection httpURLConnection) {
                callback.call(httpURLConnection);
                return null;
            }
        });
    }

    public <R> R doRequest(final URL url, final Function<HttpURLConnection, R> function) throws IOException {
        Assert.requireNonNull(function, "function");
        final HttpURLConnection connection = createConnectionForRequest(url);
        try {
            return function.call(connection);
        } finally {
            handleResponse(connection);
        }
    }
}
