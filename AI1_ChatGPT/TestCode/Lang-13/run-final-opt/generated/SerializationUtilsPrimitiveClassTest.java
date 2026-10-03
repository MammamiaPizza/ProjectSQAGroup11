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
}
