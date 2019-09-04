package com.bisna.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

public final class KSUID implements Serializable, Comparable<KSUID> {
    static final int EPOCH = 1400000000;
    static final int TIMESTAMP_LENGTH = 4;
    static final int PAYLOAD_LENGTH = 16;
    static final int MAX_ENCODED_LENGTH = 27;

    /*
     * The random number generator used by this class to create random
     * based KSUIDs. In a holder class to defer initialization until needed.
     */
    static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
        static final Base62 base62 = Base62.createInstance();
    }

    private final String value;
    private final byte[] buffer;

    private Integer timestamp;
    private byte[] payload;

    private KSUID(final String value) {
        this(value, Holder.base62.decode(value.getBytes(StandardCharsets.UTF_8)));
    }

    private KSUID(final byte[] buffer) {
        this(new String(Holder.base62.encode(buffer), 0, MAX_ENCODED_LENGTH), buffer);
    }

    private KSUID(final String value, final byte[] buffer) {
        if (value.length() != MAX_ENCODED_LENGTH) {
            throw new RuntimeException(String.format("Valid encoded KSUIDs are %s characters.", MAX_ENCODED_LENGTH));
        }

        final int byteLength = TIMESTAMP_LENGTH + PAYLOAD_LENGTH;

        if (buffer.length != byteLength) {
            throw new RuntimeException(
                String.format("Valid KSUIDs are %s bytes, but found %s bytes.", byteLength, buffer.length)
            );
        }

        this.value = value;
        this.buffer = buffer;
    }

    public static KSUID fromString(final String value) {
        return new KSUID(value);
    }

    public static KSUID randomKSUID() {
        final int timestamp = (int) ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000;

        return KSUID.randomKSUIDFromTimestamp(timestamp);
    }

    public static KSUID randomKSUIDFromTimestamp(final int timestamp) {
        final byte[] timestampBytes = ByteBuffer.allocate(TIMESTAMP_LENGTH).putInt(timestamp - EPOCH).array();
        final byte[] payloadBytes = new byte[PAYLOAD_LENGTH];

        Holder.numberGenerator.nextBytes(payloadBytes);

        try {
            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            output.write(timestampBytes);
            output.write(payloadBytes);

            return new KSUID(output.toByteArray());
        } catch (IOException exception) {
            throw new RuntimeException("Unable to generate KSUID.");
        }
    }

    public int timestamp() {
        if (this.timestamp == null) {
            final byte[] timestampBytes = new byte[TIMESTAMP_LENGTH];

            System.arraycopy(this.buffer, 0, timestampBytes, 0, TIMESTAMP_LENGTH);

            this.timestamp = ByteBuffer.wrap(timestampBytes).getInt() + EPOCH;
        }

        return this.timestamp;
    }

    public byte[] payload() {
        if (this.payload == null) {
            final byte[] payloadBytes = new byte[PAYLOAD_LENGTH];
            final int length = this.buffer.length - TIMESTAMP_LENGTH;

            System.arraycopy(this.buffer, TIMESTAMP_LENGTH, payloadBytes, 0, length);

            this.payload = payloadBytes;
        }

        return this.payload;
    }

    public KSUID nextKSUID() {
        return KSUID.randomKSUIDFromTimestamp(this.timestamp());
    }

    public int compareTo(final KSUID that) {
        return Integer.compare(this.timestamp(), that.timestamp());
    }

    public int hashCode() {
        final int result = Arrays.hashCode(this.payload());

        return 31 * result + this.value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if ((null == obj) || (obj.getClass() != KSUID.class)) {
            return false;
        }

        final KSUID that = (KSUID) obj;

        return this.value.equals(that.value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
