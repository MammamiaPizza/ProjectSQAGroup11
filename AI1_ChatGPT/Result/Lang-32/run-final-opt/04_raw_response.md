@org.junit.Test
public void testConstructorRejectsZeroAndEvenParameters() {
    try {
        new HashCodeBuilder(0, 1);
        org.junit.Assert.fail("A zero initial value must be rejected");
    } catch (IllegalArgumentException expected) {
    }

    try {
        new HashCodeBuilder(2, 1);
        org.junit.Assert.fail("An even initial value must be rejected");
    } catch (IllegalArgumentException expected) {
    }

    try {
        new HashCodeBuilder(1, 0);
        org.junit.Assert.fail("A zero multiplier must be rejected");
    } catch (IllegalArgumentException expected) {
    }

    try {
        new HashCodeBuilder(1, 2);
        org.junit.Assert.fail("An even multiplier must be rejected");
    } catch (IllegalArgumentException expected) {
    }
}

@org.junit.Test
public void testAppendObjectDispatchesToAllArrayOverloads() {
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new long[] { 2L, -3L }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new long[] { 2L, -3L }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new int[] { 2, -3 }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new int[] { 2, -3 }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new short[] { 2, -3 }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new short[] { 2, -3 }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new char[] { 'a', 'z' }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new char[] { 'a', 'z' }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new byte[] { 2, -3 }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new byte[] { 2, -3 }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new double[] { 1.5d, -2.5d }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new double[] { 1.5d, -2.5d }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new float[] { 1.5f, -2.5f }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new float[] { 1.5f, -2.5f }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new boolean[] { true, false }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new boolean[] { true, false }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append(new Object[] { "value", Integer.valueOf(2) }).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) new Object[] { "value", Integer.valueOf(2) }).toHashCode());
    org.junit.Assert.assertEquals(
            new HashCodeBuilder(1, 3).append((int[]) null).toHashCode(),
            new HashCodeBuilder(1, 3).append((Object) null).toHashCode());
}