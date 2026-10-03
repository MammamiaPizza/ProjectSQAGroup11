package org.apache.commons.collections4.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections4.BoundedCollection;
import org.junit.Test;

public class UnmodifiableBoundedCollectionGeneratedTest {

    @Test
    public void testBoundedFactoryWrapsAndDelegatesBoundedState() {
        final TestBoundedCollection<String> backing = new TestBoundedCollection<String>(2);
        final BoundedCollection<String> wrapped =
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(backing);

        assertFalse(wrapped.isFull());
        assertEquals(2, wrapped.maxSize());

        backing.add("one");
        assertFalse(wrapped.isFull());

        backing.add("two");
        assertTrue(wrapped.isFull());
        assertEquals(Arrays.asList("one", "two"), new ArrayList<String>(wrapped));
    }

    @Test
    public void testBoundedFactoryIsIdempotentForAlreadyUnmodifiableCollection() {
        final TestBoundedCollection<String> backing = new TestBoundedCollection<String>(3);
        final BoundedCollection<String> wrapped =
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(backing);

        assertSame(wrapped,
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(wrapped));
    }

    @Test
    public void testCollectionFactoryWrapsAlreadyUnmodifiableCollection() {
        final TestBoundedCollection<String> backing = new TestBoundedCollection<String>(3);
        final BoundedCollection<String> wrapped =
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(backing);
        final Collection<String> asCollection = wrapped;

        final BoundedCollection<String> rewrapped =
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(asCollection);

        assertEquals(Arrays.asList(), new ArrayList<String>(rewrapped));
        assertEquals(wrapped.maxSize(), rewrapped.maxSize());
    }

    @Test
    public void testCollectionFactoryAcceptsBoundedCollectionTypedAsCollection() {
        final TestBoundedCollection<String> backing = new TestBoundedCollection<String>(2);
        backing.add("value");
        final Collection<String> asCollection = backing;

        final BoundedCollection<String> wrapped =
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(asCollection);

        assertEquals(2, wrapped.maxSize());
        assertFalse(wrapped.isFull());
        assertEquals(Arrays.asList("value"), new ArrayList<String>(wrapped));
    }

    @Test
    public void testAllCollectionMutatorsAreRejected() {
        final TestBoundedCollection<String> backing = new TestBoundedCollection<String>(4);
        backing.add("one");
        backing.add("two");
        final BoundedCollection<String> wrapped =
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(backing);

        assertUnsupported(new Action() {
            @Override
            public void run() {
                wrapped.add("three");
            }
        });
        assertUnsupported(new Action() {
            @Override
            public void run() {
                wrapped.addAll(Arrays.asList("three", "four"));
            }
        });
        assertUnsupported(new Action() {
            @Override
            public void run() {
                wrapped.remove("one");
            }
        });
        assertUnsupported(new Action() {
            @Override
            public void run() {
                wrapped.removeAll(Arrays.asList("one"));
            }
        });
        assertUnsupported(new Action() {
            @Override
            public void run() {
                wrapped.retainAll(Arrays.asList("one"));
            }
        });
        assertUnsupported(new Action() {
            @Override
            public void run() {
                wrapped.clear();
            }
        });

        assertEquals(Arrays.asList("one", "two"), new ArrayList<String>(backing));
    }

    @Test
    public void testIteratorIsUnmodifiableAndStillIteratesContents() {
        final TestBoundedCollection<String> backing = new TestBoundedCollection<String>(2);
        backing.add("first");
        backing.add("second");
        final BoundedCollection<String> wrapped =
                UnmodifiableBoundedCollection.unmodifiableBoundedCollection(backing);

        final Iterator<String> iterator = wrapped.iterator();
        assertEquals("first", iterator.next());

        assertUnsupported(new Action() {
            @Override
            public void run() {
                iterator.remove();
            }
        });

        assertEquals("second", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(Arrays.asList("first", "second"), new ArrayList<String>(backing));
    }

    @Test
    public void testCollectionFactoryRejectsNullAndNonBoundedCollections() {
        try {
            UnmodifiableBoundedCollection.unmodifiableBoundedCollection((Collection<String>) null);
            fail("Null collections must be rejected");
        } catch (final IllegalArgumentException expected) {
            // expected
        }

        try {
            UnmodifiableBoundedCollection.unmodifiableBoundedCollection(
                    (Collection<String>) new ArrayList<String>());
            fail("Non-bounded collections must be rejected");
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

    private static void assertUnsupported(final Action action) {
        try {
            action.run();
            fail("Operation must be unsupported");
        } catch (final UnsupportedOperationException expected) {
            // expected
        }
    }

    private interface Action {
        void run();
    }

    private static final class TestBoundedCollection<E> extends ArrayList<E>
            implements BoundedCollection<E> {
        private static final long serialVersionUID = 1L;

        private final int maximumSize;

        TestBoundedCollection(final int maximumSize) {
            this.maximumSize = maximumSize;
        }

        @Override
        public boolean add(final E element) {
            if (isFull()) {
                throw new IllegalStateException("Collection is full");
            }
            return super.add(element);
        }

        @Override
        public boolean isFull() {
            return size() >= maximumSize;
        }

        @Override
        public int maxSize() {
            return maximumSize;
        }
    }
}