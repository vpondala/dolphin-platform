package com.canoo.dp.impl.server.event;

import com.canoo.dp.impl.server.bootstrap.PlatformBootstrap;
import com.canoo.platform.remoting.server.event.EventBus;
import com.canoo.platform.server.spi.ServerCoreComponents;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LazyEventBusInvocationHandler implements InvocationHandler {

    private final static String DUMMY_OBJECT = "";

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final ServerCoreComponents serverCoreComponents = PlatformBootstrap.getServerCoreComponents();
        if(serverCoreComponents != null) {
            final EventBus instance = serverCoreComponents.getInstance(EventBus.class);
            if (instance != null) {
                return method.invoke(instance, args);
            }
        }
        if(method.getDeclaringClass().equals(Object.class)) {
            return method.invoke(DUMMY_OBJECT, args);
        }
        if (method.getName().equals("subscribe")) {
            throw new IllegalStateException("Subscription can only be done from Dolphin Context!");
        } else {
            return null;
        }
    }
}
