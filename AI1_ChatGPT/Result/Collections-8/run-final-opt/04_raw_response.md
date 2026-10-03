@org.junit.Test
public void testIteratorRemovalAcrossWrappedTailRetainsIterationOrder() {
    UnboundedFifoBuffer buffer = new UnboundedFifoBuffer(3);
    buffer.add("a");
    buffer.add("b");
    buffer.add("c");
    buffer.remove();
    buffer.add("d");

    java.util.Iterator iterator = buffer.iterator();
    org.junit.Assert.assertEquals("b", iterator.next());
    org.junit.Assert.assertEquals("c", iterator.next());
    iterator.remove();

    org.junit.Assert.assertEquals(2, buffer.size());
    org.junit.Assert.assertTrue(iterator.hasNext());
    org.junit.Assert.assertEquals("d", iterator.next());
    org.junit.Assert.assertFalse(iterator.hasNext());
    org.junit.Assert.assertEquals("b", buffer.remove());
    org.junit.Assert.assertEquals("d", buffer.remove());
    org.junit.Assert.assertTrue(buffer.isEmpty());
}