package com.bisna.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(BlockJUnit4ClassRunner.class)
public class Base62Test {
    private final Base62 standardEncoder = Base62.createInstance();

    private final Base62[] encoders = {
        Base62.createInstanceWithGmpCharacterSet(),
        Base62.createInstanceWithInvertedCharacterSet()
    };

    @Test
    public void preservesIdentity() {
        for (byte[] message : this.getRawInputs()) {
            for (Base62 encoder : encoders) {
                final byte[] encoded = encoder.encode(message);
                final byte[] decoded = encoder.decode(encoded);

                Assert.assertArrayEquals(message, decoded);
            }
        }
    }

    @Test
    public void alphaNumericOutput() {
        for (byte[] message : this.getRawInputs()) {
            for (Base62 encoder : encoders) {
                final byte[] encoded = encoder.encode(message);
                final String encodedStr = new String(encoded);

                Assert.assertTrue(isAlphaNumeric(encodedStr));
            }
        }
    }

    @Test
    public void emptyInputs() {
        final byte[] empty = new byte[0];

        for (Base62 encoder : encoders) {
            final byte[] encoded = encoder.encode(empty);
            Assert.assertArrayEquals(empty, encoded);

            final byte[] decoded = encoder.decode(empty);
            Assert.assertArrayEquals(empty, decoded);
        }
    }

    @Test
    public void naiveTestSet() {
        for (Map.Entry<String, String> testSetEntry : this.getNaiveTestSet().entrySet()) {
            Assert.assertEquals(encode(testSetEntry.getKey()), testSetEntry.getValue());
        }
    }

    private String encode(final String input) {
        return new String(standardEncoder.encode(input.getBytes()));
    }

    private boolean isAlphaNumeric(final String str) {
        return str.matches("^[a-zA-Z0-9]+$");
    }

    public final byte[][] getRawInputs() {
        return new byte[][]{
            createIncreasingByteArray(),
            createZeroesByteArray(512),
            createPseudoRandomByteArray(0xAB, 40),
            createPseudoRandomByteArray(0x1C, 40),
            createPseudoRandomByteArray(0xF2, 40)
        };
    }

    public Map<String, String> getNaiveTestSet() {
        final Map<String, String> testSet = new HashMap<String, String>();

        testSet.put("", "");
        testSet.put("Hello", "5TP3P3v");
        testSet.put("0123456789", "18XU2xYejWO9d3");
        testSet.put("The quick brown fox jumps over the lazy dog", "83UM8dOjD4xrzASgmqLOXTgTagvV1jPegUJ39mcYnwHwTlzpdfKXvpp4RL");
        testSet.put("Sphinx of black quartz, judge my vow", "1Ul5yQGNM8YFBp3sz19dYj1kTp95OW7jI8pTcTP5JhYjIaFmx");

        return testSet;
    }

    private byte[] createIncreasingByteArray() {
        final byte[] arr = new byte[256];
        for (int i = 0; i < 256; i++) {
            arr[i] = (byte) (i & 0xFF);
        }
        return arr;
    }

    private byte[] createZeroesByteArray(int size) {
        return new byte[size];
    }

    private byte[] createPseudoRandomByteArray(int seed, int size) {
        final byte[] arr = new byte[size];
        int state = seed;
        for (int i = 0; i < size; i += 4) {
            state = xorshift(state);
            for (int j = 0; j < 4 && i + j < size; j++) {
                arr[i + j] = (byte) ((state >> j) & 0xFF);
            }
        }
        return arr;
    }

    private int xorshift(int x) {
        x ^= (x << 13);
        x ^= (x >> 17);
        x ^= (x << 5);
        return x;
    }
}
