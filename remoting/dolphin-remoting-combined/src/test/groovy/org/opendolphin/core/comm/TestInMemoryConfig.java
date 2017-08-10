package org.opendolphin.core.comm;

import core.comm.DefaultInMemoryConfig;
import org.opendolphin.core.client.comm.RunLaterUiThreadHandler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

public class TestInMemoryConfig extends DefaultInMemoryConfig {

    /**
     * needed since tests should run fully asynchronous but we have to wait at the end of the test
     */
    private CountDownLatch done = new CountDownLatch(1);

    public TestInMemoryConfig() {
        this(new RunLaterUiThreadHandler());
    }

    public TestInMemoryConfig(Executor uiExecutor) {
        super(uiExecutor);
        getServerDolphin().getServerConnector().registerDefaultActions();
        getClientConnector().setSleepMillis(0);
    }

    public void assertionsDone() {
        done.countDown();
    }

    /**
     * make sure we continue only after all previous commands have been executed
     */
    public void syncPoint(final int soManyRoundTrips) {
        if (soManyRoundTrips < 1) {
            return;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        getClientDolphin().sync(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
                syncPoint((int) soManyRoundTrips - 1);
            }

        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public CountDownLatch getDone() {
        return done;
    }

}
