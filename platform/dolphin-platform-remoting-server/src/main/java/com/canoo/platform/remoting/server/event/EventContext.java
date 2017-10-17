package com.canoo.platform.remoting.server.event;

public interface EventContext {

    String getProviderType();

    String getProviderId();

    long getSendTimestamp();

}
