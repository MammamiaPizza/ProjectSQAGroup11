package org.apache.commons.lang3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.ByteArrayInputStream;

import org.junit.Test;

public class SerializationUtilsPrimitiveClassTest {

    @Test
    public void cloneIntPrimitiveClass() {
        Class<Integer> cloned = SerializationUtils.clone(int.class);

        assertSame(int.class, cloned);
    }

    @Test
    public void cloneBooleanPrimitiveClass() {
        Class<Boolean> cloned = SerializationUtils.clone(boolean.class);

        assertSame(boolean.class, cloned);
    }

    @Test
    public void cloneVoidPrimitiveClass() {
        Class<Void> cloned = SerializationUtils.clone(void.class);

        assertSame(void.class, cloned);
    }

    @Test
    public void cloneReferenceClassPreservesClassObject() {
        Class<String> cloned = SerializationUtils.clone(String.class);

        assertSame(String.class, cloned);
    }

    @Test
    public void deserializePrimitiveClassesFromByteArray() {
        assertSame(int.class, SerializationUtils.deserialize(SerializationUtils.serialize(int.class)));
        assertSame(boolean.class, SerializationUtils.deserialize(SerializationUtils.serialize(boolean.class)));
        assertSame(void.class, SerializationUtils.deserialize(SerializationUtils.serialize(void.class)));
    }

    @Test
    public void deserializePrimitiveClassesFromInputStream() {
        byte[] intData = SerializationUtils.serialize(int.class);
        byte[] booleanData = SerializationUtils.serialize(boolean.class);
        byte[] voidData = SerializationUtils.serialize(void.class);

        assertSame(int.class, SerializationUtils.deserialize(new ByteArrayInputStream(intData)));
        assertSame(boolean.class, SerializationUtils.deserialize(new ByteArrayInputStream(booleanData)));
        assertSame(void.class, SerializationUtils.deserialize(new ByteArrayInputStream(voidData)));
    }

    @Test
    public void cloneNullReturnsNull() {
        assertNull(SerializationUtils.clone(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNullByteArrayIsRejected() {
        SerializationUtils.deserialize((byte[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserializeNullInputStreamIsRejected() {
        SerializationUtils.deserialize((java.io.InputStream) null);
    }

    @Test
    public void serializedReferenceClassDeserializesCorrectly() {
        Object deserialized = SerializationUtils.deserialize(SerializationUtils.serialize(String.class));

        assertEquals(String.class, deserialized);
    }

@Test(expected = IllegalArgumentException.class)
public void serializeRejectsNullOutputStream() {
    org.apache.commons.lang3.SerializationUtils.serialize("value", null);
}

@Test
public void serializeWrapsOutputStreamIOException() {
    try {
        org.apache.commons.lang3.SerializationUtils.serialize("value", new java.io.OutputStream() {
            @Override
            public void write(final int b) throws java.io.IOException {
                throw new java.io.IOException("write failure");
            }
        });
        org.junit.Assert.fail("Expected SerializationException");
    } catch (org.apache.commons.lang3.SerializationException ex) {
        org.junit.Assert.assertTrue(ex.getCause() instanceof java.io.IOException);
    }
}

@Test
public void deserializeWrapsMalformedStreamIOException() {
    try {
        org.apache.commons.lang3.SerializationUtils.deserialize(new byte[] { 1 });
        org.junit.Assert.fail("Expected SerializationException");
    } catch (org.apache.commons.lang3.SerializationException ex) {
        org.junit.Assert.assertTrue(ex.getCause() instanceof java.io.IOException);
    }
}

@Test
public void deserializeIgnoresInputStreamCloseIOException() {
    final byte[] data = org.apache.commons.lang3.SerializationUtils.serialize("value");

    final Object deserialized = org.apache.commons.lang3.SerializationUtils.deserialize(
            new java.io.ByteArrayInputStream(data) {
                @Override
                public void close() throws java.io.IOException {
                    throw new java.io.IOException("close failure");
                }
            });

    org.junit.Assert.assertEquals("value", deserialized);
}
}
