package com.canoo.dp.impl.platform.client;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.client.UrlToAppDomainConverter;

import java.net.URL;

public class SimpleUrlToAppDomainConverter implements UrlToAppDomainConverter {

    @Override
    public String getApplicationDomain(URL url) {
        return Assert.requireNonNull(url, "url").getHost() + ":" + url.getPort();
    }
}
