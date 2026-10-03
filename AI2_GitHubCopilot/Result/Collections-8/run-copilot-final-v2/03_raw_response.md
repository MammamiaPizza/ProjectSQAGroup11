package org.apache.commons.collections.buffer;

 import static org.junit.Assert.*;
 import static org.hamcrest.CoreMatchers.is;
 import static org.hamcrest.MatcherAssert.assertThat;

 import org.junit.Test;
 import org.apache.commons.collections.BufferUnderflowException;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;

 public class TestUnboundedFifoBufferBug220 {

     private UnboundedFifoBuffer copy(UnboundedFifoBuffer original) throws Exception {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(original);
         oos.close();

         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bais);
         UnboundedFifoBuffer result = (UnboundedFifoBuffer) ois.readObject();
         ois.close();
         return result;
     }

     @Test
     public void testSerializeDeserializeOneElement() throws Exception {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer();
         buf.add("element");
         assertThat(buf.size(), is(1));

         UnboundedFifoBuffer copy = copy(buf);
         assertThat("size after deserialization", copy.size(), is(1));
         assertThat(copy.get(), is((Object) "element"));
     }

     @Test
     public void testSerializeDeserializeEmpty() throws Exception {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer();
         assertThat(buf.size(), is(0));

         UnboundedFifoBuffer copy = copy(buf);
         assertThat(copy.size(), is(0));
         assertTrue(copy.isEmpty());
     }

     @Test
     public void testSerializeDeserializeMultipleElements() throws Exception {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer();
         String[] values = {"a", "b", "c"};
         for (String v : values) { buf.add(v); }
         assertThat(buf.size(), is(values.length));

         UnboundedFifoBuffer copy = copy(buf);
         assertThat(copy.size(), is(values.length));
         Iterator it = copy.iterator();
         for (int i = 0; i < values.length; i++) {
             assertThat(it.next(), is((Object) values[i]));
         }
         assertFalse(it.hasNext());
     }

     @Test
     public void testSerializeDeserializeWrapaound() throws Exception {
         // Force wrap by using small initial capacity
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer(2);
         buf.add("a");
         buf.add("b");
         assertEquals("a", buf.remove()); // head moves forward
         buf.add("c");
         buf.add("d"); // tail wraps
         assertThat(buf.size(), is(3));

         UnboundedFifoBuffer copy = copy(buf);
         assertThat(copy.size(), is(3));
         Iterator it = copy.iterator();
         assertEquals("b", it.next());
         assertEquals("c", it.next());
         assertEquals("d", it.next());
         assertFalse(it.hasNext());
     }

     @Test
     public void testSerializeDeserializeMultipleCyles() throws Exception {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer();
         buf.add("x");
         buf.add("y");
         UnboundedFifoBuffer copy1 = copy(buf);
         UnboundedFifoBuffer copy2 = copy(copy1);
         assertThat(copy2.size(), is(2));
         assertThat(copy2.get(), is((Object) "x"));
     }

     @Test
     public void testAddAfterDeserializationIncrementsSize() throws Exception {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer();
         buf.add("original");
         UnboundedFifoBuffer copy = copy(buf);
         assertThat(copy.size(), is(1));
         copy.add("additional");
         assertThat(copy.size(), is(2));
         assertThat(copy.get(), is((Object) "original"));
     }

     @Test
     public void testRemoveAfterDeserializationWorks() throws Exception {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer();
         buf.add("first");
         buf.add("second");
         buf.add("third");
         UnboundedFifoBuffer copy = copy(buf);
         assertThat(copy.size(), is(3));
         assertEquals("first", copy.remove());
         assertThat(copy.size(), is(2));
         assertThat(copy.get(), is((Object) "second"));
     }

     @Test
     public void testGetAfterDeserializationPreservesSize() throws Exception {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer();
         buf.add("value");
         UnboundedFifoBuffer copy = copy(buf);
         assertThat(copy.get(), is((Object) "value"));
         assertThat(copy.size(), is(1)); // get() does not remove
     }

     @Test(expected = NullPointerException.class)
     public void testAddNullThrowsNullPointerException() {
         new UnboundedFifoBuffer().add(null);
     }

     @Test(expected = BufferUnderflowException.class)
     public void testGetOnEmptyThrowsBufferUnderflowException() {
         new UnboundedFifoBuffer().get();
     }

     @Test(expected = BufferUnderflowException.class)
     public void testRemoveOnEmptyThrowsBufferUnderflowException() {
         new UnboundedFifoBuffer().remove();
     }

     @Test
     public void testIteratorPreservesInsertionOrder() {
         UnboundedFifoBuffer buf = new UnboundedFifoBuffer(3);
         buf.add("1");
         buf.add("2");
         buf.add("3");
         Iterator it = buf.iterator();
         assertTrue(it.hasNext());
         assertEquals("1", it.next());
         assertTrue(it.hasNext());
         assertEquals("2", it.next());
         assertTrue(it.hasNext());
         assertEquals("3", it.next());
         assertFalse(it.hasNext());
     }
 }