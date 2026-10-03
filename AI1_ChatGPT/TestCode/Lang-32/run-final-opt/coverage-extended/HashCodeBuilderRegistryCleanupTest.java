package org.apache.commons.lang3.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

public class HashCodeBuilderRegistryCleanupTest {

    private interface CheckedAction {
        void run() throws Exception;
    }

    private static final class SelfReferencingObject {
        private final SelfReferencingObject self = this;

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    private static final class MutualReferenceObject {
        private MutualReferenceObject other;

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    private static final class SimpleObject {
        private final int value = 5;
    }

    @Test
    public void testSelfReferencingObjectClearsRegistryAfterEachReflectionHashCode() {
        runInFreshThread(new CheckedAction() {
            @Override
            public void run() {
                SelfReferencingObject object = new SelfReferencingObject();
                int expected = HashCodeBuilder.reflectionHashCode(object);
                assertNull("Registry must be removed after hashing a self cycle",
                        HashCodeBuilder.getRegistry());

                for (int i = 0; i < 3; i++) {
                    assertEquals(expected, HashCodeBuilder.reflectionHashCode(object));
                    assertNull("Registry must be removed after every cyclic hash invocation",
                            HashCodeBuilder.getRegistry());
                }
            }
        });
    }

    @Test
    public void testMutuallyReferencingObjectsClearRegistryAfterReflectionHashCode() {
        runInFreshThread(new CheckedAction() {
            @Override
            public void run() {
                MutualReferenceObject first = new MutualReferenceObject();
                MutualReferenceObject second = new MutualReferenceObject();
                first.other = second;
                second.other = first;

                int firstHash = HashCodeBuilder.reflectionHashCode(first);
                assertNull("Registry must be removed after hashing a mutual cycle",
                        HashCodeBuilder.getRegistry());

                assertEquals(firstHash, HashCodeBuilder.reflectionHashCode(first));
                assertNull("Registry must remain absent after repeated mutual-cycle hashing",
                        HashCodeBuilder.getRegistry());
            }
        });
    }

    @Test
    public void testAcyclicReflectionHashCodeHasExpectedValueAndClearsRegistry() {
        runInFreshThread(new CheckedAction() {
            @Override
            public void run() {
                SimpleObject object = new SimpleObject();

                assertEquals(634, HashCodeBuilder.reflectionHashCode(object));
                assertNull("Registry must be removed after normal reflection hashing",
                        HashCodeBuilder.getRegistry());
            }
        });
    }

    @Test
    public void testReflectionHashCodeRejectsNullObject() {
        try {
            HashCodeBuilder.reflectionHashCode((Object) null);
            fail("A null object must be rejected");
        } catch (IllegalArgumentException expected) {
            assertEquals("The object to build a hash code for must not be null",
                    expected.getMessage());
        }
    }

    private static void runInFreshThread(final CheckedAction action) {
        final Throwable[] failure = new Throwable[1];
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    action.run();
                } catch (Throwable ex) {
                    failure[0] = ex;
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            fail("Interrupted while waiting for test thread");
        }

        if (failure[0] != null) {
            fail(failure[0].toString());
        }
    }

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
}
