package com.canoo.platform.remoting.server.event.impl;

import com.canoo.platform.remoting.server.event.EventContext;
import com.canoo.platform.remoting.server.event.EventFilter;
import com.canoo.platform.remoting.server.event.Message;

import java.io.Serializable;

public interface Event extends Serializable{

    Message getMessage();

    EventContext getContext();

    EventFilter getFilter();
}
