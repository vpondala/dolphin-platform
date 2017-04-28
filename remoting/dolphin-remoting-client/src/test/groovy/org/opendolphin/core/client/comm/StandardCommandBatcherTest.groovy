package org.opendolphin.core.client.comm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardCommandBatcherTest {

    private CommandBatcher batcher;

    @Before
    public void setUp() throws Exception {
        batcher = new CommandBatcher();
    }

    @Test
    public void testEmpty() {
        Assert.assertTrue(batcher.isEmpty());
    }

    @Test
    public void testOne() {
        CommandAndHandler cah = new CommandAndHandler(null);
        batcher.batch(cah);
        try {
            Assert.assertEquals(Collections.singletonList(cah), batcher.getWaitingBatches().getVal());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultipleDoesNotBatch() {

        List<CommandAndHandler> list = Arrays.asList(new CommandAndHandler(null), new CommandAndHandler(null), new CommandAndHandler(null));
        for (CommandAndHandler cwh : list) {
            batcher.batch(cwh);
        }

        try {
            Assert.assertEquals(Collections.singletonList(list.get(0)), batcher.getWaitingBatches().getVal());
            Assert.assertEquals(Collections.singletonList(list.get(1)), batcher.getWaitingBatches().getVal());
            Assert.assertEquals(Collections.singletonList(list.get(2)), batcher.getWaitingBatches().getVal());
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

}
