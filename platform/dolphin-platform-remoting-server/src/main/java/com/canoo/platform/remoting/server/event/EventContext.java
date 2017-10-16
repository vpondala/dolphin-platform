package com.canoo.platform.remoting.server.event;

import java.io.Serializable;

public interface EventContext {

    Serializable getProvider();

    long getSendTimestamp();

}
