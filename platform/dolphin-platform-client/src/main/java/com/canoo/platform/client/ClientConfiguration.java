package com.canoo.platform.client;

import com.canoo.platform.core.http.HttpURLConnectionFactory;
import com.canoo.platform.core.PlatformConfiguration;

import java.net.CookieStore;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public interface ClientConfiguration extends PlatformConfiguration {

    String BACKGROUND_EXECUTOR = "platform.background.executor";

    String UNCAUGHT_EXCEPTION_HANDLER = "platform.background.uncaughtExceptionHandler";

    String UI_EXECUTOR = "platform.ui.executor";

    String UI_UNCAUGHT_EXCEPTION_HANDLER = "platform.ui.uncaughtExceptionHandler";

    String COOKIE_STORE = "platform.http.cookieStore";

    String CONNECTION_FACTORY = "platform.http.connectionFactory";

    Executor getUiExecutor();

    ExecutorService getBackgroundExecutor();

    Thread.UncaughtExceptionHandler getUncaughtExceptionHandler();

    Thread.UncaughtExceptionHandler getUiUncaughtExceptionHandler();

    CookieStore getCookieStore();

    HttpURLConnectionFactory getHttpURLConnectionFactory();

    void setCookieStore(CookieStore cookieStore);

    void setUiExecutor(Executor executor);

    void setBackgroundExecutor(ExecutorService service);

    void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler);

    void setUiUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler);

    void setHttpURLConnectionFactory(HttpURLConnectionFactory httpURLConnectionFactory);

    void setStringProperty(final String key, final String value);

    void setBooleanProperty(final String key, boolean value);

    void setIntProperty(final String key, int value);

    void setLongProperty(final String key, long value);

    void setListProperty(final String key, final List<String> value);

    <T> void setObjectProperty(final String key, T value);

    <T> T getObjectProperty(final String key);

    <T> T getObjectProperty(final String key, final T defaultValue);
}
