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
}