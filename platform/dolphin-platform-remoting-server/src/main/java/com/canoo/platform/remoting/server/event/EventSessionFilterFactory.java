package com.canoo.platform.remoting.server.event;

import com.canoo.dp.impl.platform.core.Assert;

public class EventSessionFilterFactory {

    public static EventSessionFilter allowClientSessions(final String... clientSessionIds) {
        return new ListEventSessionFilter(clientSessionIds);
    }

    public static EventSessionFilter excludeClientSessions(final String... clientSessionIds) {
        return not(allowClientSessions(clientSessionIds));
    }

    public static EventSessionFilter not(final EventSessionFilter filter) {
        Assert.requireNonNull(filter, "filter");
        return new EventSessionFilter() {

            @Override
            public boolean shouldHandleEvent(final String sessionId) {
                return !filter.shouldHandleEvent(sessionId);
            }
        };
    }

    public static EventSessionFilter and(final EventSessionFilter... filters) {
        return new EventSessionFilter() {
            @Override
            public boolean shouldHandleEvent(String sessionId) {
                for(EventSessionFilter filter : filters) {
                    if(!filter.shouldHandleEvent(sessionId)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static EventSessionFilter or(final EventSessionFilter... filters) {
        return new EventSessionFilter() {
            @Override
            public boolean shouldHandleEvent(String sessionId) {
                for(EventSessionFilter filter : filters) {
                    if(filter.shouldHandleEvent(sessionId)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

}
