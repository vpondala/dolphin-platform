package com.canoo.dp.impl.platform.client;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.platform.core.PlatformConstants;
import com.canoo.platform.client.HttpURLConnectionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;

public class ClientSessionSupportingURLConnectionRequestHandler implements HttpURLConnectionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionSupportingURLConnectionRequestHandler.class);

    private final ClientSessionStore clientSessionStore;

    public ClientSessionSupportingURLConnectionRequestHandler(final ClientSessionStore clientSessionStore) {
        this.clientSessionStore = Assert.requireNonNull(clientSessionStore, "clientSessionStore");
    }

    @Override
    public void handle(final HttpURLConnection request) {
        Assert.requireNonNull(request, "request");
        final String clientId = clientSessionStore.getClientIdentifierForUrl(request.getURL());
        if (clientId != null) {
            LOG.debug("Adding client id {} to http request", clientId);
            request.setRequestProperty(PlatformConstants.CLIENT_ID_HTTP_HEADER_NAME, clientId);
        } else {
            LOG.debug("Sending first request to application at {}. Dolphin client id not defined.", request.getURL());
        }
    }
}
