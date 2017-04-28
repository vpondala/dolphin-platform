package org.opendolphin.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.*;

public class ModelStoreConfigTest {

    private ModelStoreConfig modelStoreConfig;

    @Before
    public void setUp() {
        modelStoreConfig = new ModelStoreConfig();
    }

    private String getLog(Runnable runnable) {
        Logger logger = Logger.getLogger(ModelStoreConfig.class.getName());
        try(ByteArrayOutputStream out = new ByteArrayOutputStream(1024)) {
            Handler stringHandler = new StreamHandler(out, new SimpleFormatter());
            stringHandler.setLevel(Level.WARNING);
            logger.addHandler(stringHandler);

            runnable.run();

            ((StreamHandler) stringHandler).flush();
            logger.removeHandler(stringHandler);
            return out.toString();
        } catch (IOException e) {
            Assert.fail(e.getMessage());
            throw new RuntimeException("error", e);
        }
    }

    @Test
    public void testDefaultCapacitiesPowerOfTwo() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                new ModelStoreConfig();
            }

        });
        Assert.assertTrue(log1.isEmpty());
    }

    @Test
    public void testAttributeCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setAttributeCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getAttributeCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setAttributeCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("attributeCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getAttributeCapacity());
    }

    @Test
    public void testSetPmCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setPmCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getPmCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setPmCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("pmCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getPmCapacity());
    }

    @Test
    public void testQualifierCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setQualifierCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getQualifierCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setQualifierCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("qualifierCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getQualifierCapacity());
    }

    @Test
    public void testTypeCapacity() {
        // no warn message should be logged
        String log1 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setTypeCapacity(4);
            }

        });
        Assert.assertTrue(log1.isEmpty());
        Assert.assertEquals(4, modelStoreConfig.getTypeCapacity());

        // a warn message should be logged
        String log2 = getLog(new Runnable() {
            @Override
            public void run() {
                modelStoreConfig.setTypeCapacity(5);
            }

        });
        Assert.assertTrue(log2.contains("typeCapacity"));
        Assert.assertEquals(5, modelStoreConfig.getTypeCapacity());
    }
}
