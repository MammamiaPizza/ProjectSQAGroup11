package org.apache.commons.math3.random;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class BitsStreamGeneratorSerializationTest {

    @Test
    public void testSerializationPreservesCachedGaussianValue() throws Exception {
        MersenneTwister original = new MersenneTwister(123456789L);

        original.nextGaussian();
        MersenneTwister copy = serializeAndRestore(original);

        assertEquals(original.nextGaussian(), copy.nextGaussian(), 0.0);
    }

    @Test
    public void testSerializationPreservesStateAfterGaussianCacheIsConsumed() throws Exception {
        MersenneTwister original = new MersenneTwister(987654321L);

        original.nextGaussian();
        original.nextGaussian();
        MersenneTwister copy = serializeAndRestore(original);

        assertEquals(original.nextGaussian(), copy.nextGaussian(), 0.0);
        assertEquals(original.nextDouble(), copy.nextDouble(), 0.0);
    }

    @Test
    public void testClearRemovesCachedGaussianBeforeSerialization() throws Exception {
        MersenneTwister original = new MersenneTwister(246813579L);

        original.nextGaussian();
        original.clear();
        MersenneTwister copy = serializeAndRestore(original);

        assertEquals(original.nextGaussian(), copy.nextGaussian(), 0.0);
    }

    private MersenneTwister serializeAndRestore(MersenneTwister generator) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(generator);
        output.close();

        ObjectInputStream input =
                new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        MersenneTwister restored = (MersenneTwister) input.readObject();
        input.close();
        return restored;
    }
}
