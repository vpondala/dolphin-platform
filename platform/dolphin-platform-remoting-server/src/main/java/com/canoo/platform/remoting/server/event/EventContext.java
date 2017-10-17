package com.canoo.platform.remoting.server.event;

import java.io.Serializable;

public interface EventContext extends Serializable{

    String getProviderType();

    String getProviderId();

    long getSendTimestamp();

}
