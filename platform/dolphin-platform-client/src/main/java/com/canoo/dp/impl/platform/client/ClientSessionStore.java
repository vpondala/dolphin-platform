package com.canoo.dp.impl.platform.client;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.UrlToAppDomainConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientSessionStore {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSessionStore.class);

    private final UrlToAppDomainConverter converter;

    private Lock mapLock = new ReentrantLock();

    private final Map<String, String> domainToId = new HashMap<>();

    public ClientSessionStore(final UrlToAppDomainConverter converter) {
        this.converter = Assert.requireNonNull(converter, "converter");
    }

    public String getClientIdentifierForUrl(final URL url) {
        Assert.requireNonNull(url, "url");
        final String applicationDomain = converter.getApplicationDomain(url);
        if(applicationDomain == null) {
            throw new IllegalStateException("Can not define application domain for url " + url);
        }
        LOG.debug("application domain: {}", applicationDomain);
        mapLock.lock();
        try {
            return domainToId.get(applicationDomain);
        } finally {
            mapLock.unlock();
        }
    }

    public void addClientIdForUrl(final URL url, final String clientId) {
        final String applicationDomain = converter.getApplicationDomain(url);
        if(applicationDomain == null) {
            throw new IllegalStateException("Can not define application domain for url " + url);
        }
        mapLock.lock();
        try {
            if (domainToId.containsKey(applicationDomain)) {
                String storedId = domainToId.get(applicationDomain);
                if (!storedId.equals(clientId)) {
                    throw new IllegalStateException("Client Id for application domain " + applicationDomain + " already specified.");
                }
            } else {
                LOG.debug("Defining client id {} for application domain {}", clientId, applicationDomain);
                domainToId.put(applicationDomain, clientId);
            }
        } finally {
            mapLock.unlock();
        }
    }

    public void resetSession(URL url) {
        final String applicationDomain = converter.getApplicationDomain(url);
        if(applicationDomain == null) {
            throw new IllegalStateException("Can not define application domain for url " + url);
        }
        LOG.debug("resetting application domain: {}", applicationDomain);
        mapLock.lock();
        try {
            domainToId.remove(applicationDomain);
        } finally {
            mapLock.unlock();
        }
    }
}
