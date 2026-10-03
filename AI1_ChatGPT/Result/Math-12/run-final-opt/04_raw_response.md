@org.junit.Test
public void testNextBytesUsesLittleEndianBytesAndHandlesRemainder() {
    final FixedBitsGenerator generator =
        new FixedBitsGenerator(0x01020304, 0xa0b0c0d0);
    final byte[] bytes = new byte[6];

    generator.nextBytes(bytes);

    org.junit.Assert.assertArrayEquals(
        new byte[] { 4, 3, 2, 1, (byte) 0xd0, (byte) 0xc0 }, bytes);
    org.junit.Assert.assertEquals(2, generator.calls());
    org.junit.Assert.assertEquals(32, generator.requestedBits(0));
    org.junit.Assert.assertEquals(32, generator.requestedBits(1));
}

@org.junit.Test
public void testNextDoubleCombinesBothRequestedBitStreams() {
    final FixedBitsGenerator generator = new FixedBitsGenerator(1, 1);

    final double value = generator.nextDouble();

    org.junit.Assert.assertEquals(
        1.0 / (1L << 26) + 1.0 / (1L << 52), value, 0.0);
    org.junit.Assert.assertEquals(2, generator.calls());
    org.junit.Assert.assertEquals(26, generator.requestedBits(0));
    org.junit.Assert.assertEquals(26, generator.requestedBits(1));
}

@org.junit.Test
public void testNextIntBoundedUsesPowerOfTwoAndRetriesRejectedValues() {
    final FixedBitsGenerator generator =
        new FixedBitsGenerator(1 << 30, Integer.MAX_VALUE, 5);

    org.junit.Assert.assertEquals(4, generator.nextInt(8));
    org.junit.Assert.assertEquals(2, generator.nextInt(3));
    org.junit.Assert.assertEquals(3, generator.calls());
    org.junit.Assert.assertEquals(31, generator.requestedBits(0));
    org.junit.Assert.assertEquals(31, generator.requestedBits(1));
    org.junit.Assert.assertEquals(31, generator.requestedBits(2));
}

@org.junit.Test(expected = org.apache.commons.math3.exception.NotStrictlyPositiveException.class)
public void testNextIntRejectsNonPositiveBound() {
    new FixedBitsGenerator(0).nextInt(0);
}

private static final class FixedBitsGenerator
    extends org.apache.commons.math3.random.BitsStreamGenerator {
    private final int[] values;
    private final int[] requested = new int[16];
    private int index;

    FixedBitsGenerator(int... values) {
        this.values = values;
    }

    @Override
    public void setSeed(int seed) {
    }

    @Override
    public void setSeed(int[] seed) {
    }

    @Override
    public void setSeed(long seed) {
    }

    @Override
    protected int next(int bits) {
        if (index >= values.length) {
            throw new IllegalStateException("No more fixed values");
        }
        requested[index] = bits;
        return values[index++];
    }

    int calls() {
        return index;
    }

    int requestedBits(int call) {
        return requested[call];
    }
}