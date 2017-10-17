package com.canoo.dolphin.integration.server.beans;

import com.canoo.dolphin.integration.bean.BeanTestBean;
import com.canoo.dolphin.integration.bean.BeanTestConstants;
import com.canoo.platform.remoting.BeanManager;
import com.canoo.platform.remoting.server.DolphinController;
import com.canoo.platform.remoting.server.DolphinModel;
import com.canoo.platform.remoting.server.RemotingContext;
import com.canoo.platform.remoting.server.binding.PropertyBinder;
import com.canoo.platform.remoting.server.event.EventBus;
import com.canoo.platform.server.client.ClientSession;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@DolphinController(BeanTestConstants.BEAN_CONTROLLER_NAME)
public class BeanTestController {

    @DolphinModel
    private BeanTestBean model;

    @Inject
    private ClientSession clientSession;

    @Inject
    private RemotingContext remotingContext;

    @Inject
    private BeanManager beanManager;

    @Inject
    private EventBus dolphinEventBus;

    @Inject
    private PropertyBinder propertyBinder;

    @PostConstruct
    public void init() {
        model.setClientSessionInjected(clientSession != null);
        model.setRemotingContextInjected(remotingContext != null);
        model.setBeanManagerInjected(beanManager != null);
        model.setDolphinEventBusInjected(dolphinEventBus != null);
        model.setPropertyBinderInjected(propertyBinder != null);

    }
}
