package com.bisna.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RunWith(BlockJUnit4ClassRunner.class)
public class KSUIDTest {
    @Test
    public void testConstruction() {
        final KSUID ksuid = KSUID.randomKSUID();
        final String value = ksuid.toString();

        Assert.assertEquals(KSUID.MAX_ENCODED_LENGTH, value.length());
    }

    @Test
    public void testConstructionWithTimestamp() {
        final int timestamp = (int) ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000;
        final KSUID ksuid = KSUID.randomKSUIDFromTimestamp(timestamp);

        Assert.assertEquals(timestamp, ksuid.timestamp());
    }

    @Test
    public void testMultipleConstructionWithTimestampProducesDifferentValues() {
        final int timestamp = (int) ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000;
        final KSUID ksuid1 = KSUID.randomKSUIDFromTimestamp(timestamp);
        final KSUID ksuid2 = KSUID.randomKSUIDFromTimestamp(timestamp);

        Assert.assertEquals(timestamp, ksuid1.timestamp());
        Assert.assertEquals(timestamp, ksuid2.timestamp());

        Assert.assertNotEquals(ksuid1.payload(), ksuid2.payload());
        Assert.assertNotEquals(ksuid1.toString(), ksuid2.toString());
    }

    @Test
    public void testConstructionWithMinimumStringEncoded() {
        final String value = "000000000000000000000000000";
        final KSUID ksuid = KSUID.fromString(value);

        Assert.assertEquals(value, ksuid.toString());
        Assert.assertEquals(KSUID.EPOCH, ksuid.timestamp());
    }

    @Test
    public void testConstructionWithLeftPaddedZeros() {
        final String value = "000000000000fsQzFP4bxwgy80V";
        final KSUID ksuid = KSUID.fromString(value);

        Assert.assertEquals(value, ksuid.toString());
        Assert.assertEquals(KSUID.EPOCH, ksuid.timestamp());
    }

    @Test
    public void testConstructionWithRightPaddedZeros() {
        final String value = "aWgEPTl1tmebfsQ000000000000";
        final KSUID ksuid = KSUID.fromString(value);

        Assert.assertEquals(value, ksuid.toString());
        Assert.assertEquals(KSUID.EPOCH - 1, ksuid.timestamp());
    }

    @Test
    public void testConstructionWithMaximumStringEncoded() {
        final String value = "aWgEPTl1tmebfsQzFP4bxwgy80V";
        final KSUID ksuid = KSUID.fromString(value);

        Assert.assertEquals(value, ksuid.toString());
        Assert.assertEquals(KSUID.EPOCH - 1, ksuid.timestamp());
    }

    @Test(expected = RuntimeException.class)
    public void testConstructionWithInvalidStringEncoded() {
        final String value = "abc123abc123abc123abc123abc123abc123";

        KSUID.fromString(value);
    }
}
