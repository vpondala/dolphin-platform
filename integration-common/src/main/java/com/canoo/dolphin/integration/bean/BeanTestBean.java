package com.canoo.dolphin.integration.bean;

import com.canoo.platform.remoting.DolphinBean;
import com.canoo.platform.remoting.Property;

@DolphinBean
public class BeanTestBean {

    private Property<Boolean> propertyBinderInjected;

    private Property<Boolean> dolphinEventBusInjected;

    private Property<Boolean> beanManagerInjected;

    private Property<Boolean> remotingContextInjected;

    private Property<Boolean> clientSessionInjected;

    public boolean getPropertyBinderInjected() {
        return propertyBinderInjected.get();
    }

    public void setPropertyBinderInjected(boolean propertyBinderInjected) {
        this.propertyBinderInjected.set(propertyBinderInjected);
    }

    public boolean getDolphinEventBusInjected() {
        return dolphinEventBusInjected.get();
    }

    public void setDolphinEventBusInjected(boolean dolphinEventBusInjected) {
        this.dolphinEventBusInjected.set(dolphinEventBusInjected);
    }

    public boolean getBeanManagerInjected() {
        return beanManagerInjected.get();
    }

    public void setBeanManagerInjected(boolean beanManagerInjected) {
        this.beanManagerInjected.set(beanManagerInjected);
    }

    public boolean getRemotingContextInjected() {
        return remotingContextInjected.get();
    }

    public void setRemotingContextInjected(boolean remotingContextInjected) {
        this.remotingContextInjected.set(remotingContextInjected);
    }

    public boolean getClientSessionInjected() {
        return clientSessionInjected.get();
    }

    public void setClientSessionInjected(boolean clientSessionInjected) {
        this.clientSessionInjected.set(clientSessionInjected);
    }
}
