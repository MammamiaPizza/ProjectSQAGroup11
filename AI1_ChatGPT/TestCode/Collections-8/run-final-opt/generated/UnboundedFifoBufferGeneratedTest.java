package org.apache.commons.collections.buffer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.BufferUnderflowException;
import org.junit.Test;

public class UnboundedFifoBufferGeneratedTest {

    @Test
    public void testSingleElementSizeGetAndRemove() {
        UnboundedFifoBuffer buffer = new UnboundedFifoBuffer();

        assertTrue(buffer.isEmpty());
        assertTrue(buffer.add("one"));
        assertEquals(1, buffer.size());
        assertFalse(buffer.isEmpty());
        assertEquals("one", buffer.get());
        assertEquals(1, buffer.size());
        assertEquals("one", buffer.remove());
        assertEquals(0, buffer.size());
        assertTrue(buffer.isEmpty());
    }

    @Test
    public void testFifoOrderAcrossGrowthAndWrapAround() {
        UnboundedFifoBuffer buffer = new UnboundedFifoBuffer(2);

        buffer.add("a");
        buffer.add("b");
        assertEquals("a", buffer.remove());

        buffer.add("c");
        buffer.add("d");
        buffer.add("e");

        assertEquals(4, buffer.size());
        assertEquals("b", buffer.remove());
        assertEquals("c", buffer.remove());
        assertEquals("d", buffer.remove());
        assertEquals("e", buffer.remove());
        assertTrue(buffer.isEmpty());
    }

    @Test
    public void testSerializationOfSingleElementPreservesSizeAndContents() throws Exception {
        UnboundedFifoBuffer original = new UnboundedFifoBuffer();
        original.add("serialized");

        UnboundedFifoBuffer restored = serializeAndRestore(original);

        assertEquals(1, restored.size());
        assertFalse(restored.isEmpty());
        assertEquals("serialized", restored.get());
        assertEquals("serialized", restored.remove());
        assertTrue(restored.isEmpty());
    }

    @Test
    public void testSerializationPreservesIterationAndRemovalOrder() throws Exception {
        UnboundedFifoBuffer original = new UnboundedFifoBuffer();
        original.add("first");
        original.add("second");
        original.add("third");

        UnboundedFifoBuffer restored = serializeAndRestore(original);

        Iterator iterator = restored.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("first", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("second", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("third", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(3, restored.size());
        assertEquals("first", restored.remove());
        assertEquals("second", restored.remove());
        assertEquals("third", restored.remove());
    }

    @Test
    public void testIteratorRemovalOfMiddleElementRetainsRemainingOrder() {
        UnboundedFifoBuffer buffer = new UnboundedFifoBuffer();
        buffer.add("first");
        buffer.add("second");
        buffer.add("third");

        Iterator iterator = buffer.iterator();
        assertEquals("first", iterator.next());
        assertEquals("second", iterator.next());
        iterator.remove();

        assertEquals(2, buffer.size());
        assertEquals("first", buffer.remove());
        assertEquals("third", buffer.remove());
        assertTrue(buffer.isEmpty());
    }

    @Test
    public void testIteratorRemovalOfHeadRemovesHead() {
        UnboundedFifoBuffer buffer = new UnboundedFifoBuffer();
        buffer.add("first");
        buffer.add("second");

        Iterator iterator = buffer.iterator();
        assertEquals("first", iterator.next());
        iterator.remove();

        assertEquals(1, buffer.size());
        assertEquals("second", buffer.get());
        assertEquals("second", buffer.remove());
    }

    @Test
    public void testEmptyBufferGetAndRemoveThrowBufferUnderflowException() {
        UnboundedFifoBuffer buffer = new UnboundedFifoBuffer();

        try {
            buffer.get();
            fail("get on an empty buffer should throw BufferUnderflowException");
        } catch (BufferUnderflowException expected) {
            assertTrue(buffer.isEmpty());
        }

        try {
            buffer.remove();
            fail("remove on an empty buffer should throw BufferUnderflowException");
        } catch (BufferUnderflowException expected) {
            assertTrue(buffer.isEmpty());
        }
    }

    @Test
    public void testNullElementsAreRejected() {
        UnboundedFifoBuffer buffer = new UnboundedFifoBuffer();

        try {
            buffer.add(null);
            fail("null elements should be rejected");
        } catch (NullPointerException expected) {
            assertTrue(buffer.isEmpty());
            assertEquals(0, buffer.size());
        }
    }

    @Test
    public void testInvalidInitialSizeIsRejected() {
        try {
            new UnboundedFifoBuffer(0);
            fail("zero initial size should be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }

        try {
            new UnboundedFifoBuffer(-1);
            fail("negative initial size should be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void testIteratorBoundaryAndIllegalRemoveStates() {
        UnboundedFifoBuffer buffer = new UnboundedFifoBuffer();
        buffer.add("item");
        Iterator iterator = buffer.iterator();

        try {
            iterator.remove();
            fail("iterator remove before next should throw IllegalStateException");
        } catch (IllegalStateException expected) {
            assertEquals(1, buffer.size());
        }

        assertEquals("item", iterator.next());
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("iterator next past end should throw NoSuchElementException");
        } catch (NoSuchElementException expected) {
            assertEquals("item", buffer.get());
        }
    }

    private UnboundedFifoBuffer serializeAndRestore(UnboundedFifoBuffer buffer) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(buffer);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        UnboundedFifoBuffer restored = (UnboundedFifoBuffer) input.readObject();
        input.close();
        return restored;
    }
}
