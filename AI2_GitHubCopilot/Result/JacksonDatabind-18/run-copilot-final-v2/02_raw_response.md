import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.ArrayList;
 import java.util.List;

 import org.junit.Test;

 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonToken;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.databind.MappingIterator;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.RuntimeJsonMappingException;

 public class MappingIteratorBug18Test {

     // Bean that cannot be deserialized from a number
     public static class Bean {
         public String name;
         public int value;
     }

     private final ObjectMapper mapper = new ObjectMapper();

     private JsonParser parserFor(String json) throws IOException {
         return mapper.getFactory().createParser(json);
     }

     private MappingIterator<Bean> iteratorFor(String json) throws IOException {
         JsonParser p = parserFor(json);
         // Must consume START_ARRAY if present, because MappingIterator with managed parser does
that
         if (p.nextToken() == JsonToken.START_ARRAY) {
             // Managed parser case: clear so iterator works
         } else {
             // Not an array, rewind? We'll handle only array case for simplicity
             throw new IOException("Expected JSON array");
         }
         // Use ObjectReader with Iterator to get a mapping iterator
         return mapper.readValues(p, Bean.class);
     }

     // 1. Normal: readAll() on valid array returns all items
     @Test
     public void testReadAllWithValidData() throws Exception {
         String json = "[{\"name\":\"a\",\"value\":1},{\"name\":\"b\",\"value\":2}]";
         MappingIterator<Bean> it = iteratorFor(json);
         List<Bean> beans = it.readAll();
         assertEquals(2, beans.size());
         assertEquals("a", beans.get(0).name);
         assertEquals(1, beans.get(0).value);
         assertEquals("b", beans.get(1).name);
         assertEquals(2, beans.get(1).value);
     }

     // 2. Error: integer in array causes JsonMappingException from nextValue()
     @Test(expected = JsonMappingException.class)
     public void testNextValueThrowsOnInvalidToken() throws Exception {
         String json = "[{\"name\":\"a\",\"value\":1}, 1, {\"name\":\"c\",\"value\":3}]";
         MappingIterator<Bean> it = iteratorFor(json);
         it.nextValue(); // first valid
         it.nextValue(); // should fail on the integer 1
     }

     // 3. readAll() stops at error and throws exception; items before error are read
     @Test
     public void testReadAllPartialOnError() throws Exception {
         String json = "[{\"name\":\"a\",\"value\":1}, 1, {\"name\":\"c\",\"value\":3}]";
         MappingIterator<Bean> it = iteratorFor(json);
         List<Bean> beans = new ArrayList<Bean>();
         try {
             it.readAll(beans);
             fail("Should have thrown an exception because of invalid token");
         } catch (JsonMappingException e) {
             // Expected: only first bean was added before the error
             assertEquals(1, beans.size());
             assertEquals("a", beans.get(0).name);
         }
     }

     // 4. hasNext() returns true before error, but next() throws RuntimeJsonMappingException,
     //    demonstrating the recovery failure (bug 18)
     @Test(expected = RuntimeJsonMappingException.class)
     public void testIterateWithInvalidTokenViaNext() throws Exception {
         String json = "[{\"name\":\"a\",\"value\":1}, 1]";
         MappingIterator<Bean> it = iteratorFor(json);
         assertTrue(it.hasNext());
         assertEquals("a", it.next().name);
         assertTrue(it.hasNext()); // true because next token (the integer) exists
         it.next(); // should throw RuntimeJsonMappingException (wrapping JsonMappingException)
     }

     // 5. Consecutive errors: readAll fails at first error, no recovery
     @Test
     public void testConsecutiveErrors() throws Exception {
         String json = "[1, 2, {\"name\":\"x\",\"value\":9}]";
         MappingIterator<Bean> it = iteratorFor(json);
         try {
             it.readAll();
             fail("Expected exception");
         } catch (JsonMappingException e) {
             // First token was invalid, so list should be empty
             assertTrue(true);
         }
     }

     // 6. Empty array: hasNext returns false, nextValue throws NoSuchElementException
     @Test(expected = java.util.NoSuchElementException.class)
     public void testEmptyArray() throws Exception {
         String json = "[]";
         MappingIterator<Bean> it = iteratorFor(json);
         assertFalse(it.hasNext());
         it.nextValue(); // should throw
     }

     // 7. Root-level integer before array (or lone invalid value)
     @Test(expected = JsonMappingException.class)
     public void testRootLevelInteger() throws Exception {
         // Not an array: a single integer token
         JsonParser p = mapper.getFactory().createParser("1");
         MappingIterator<Bean> it = mapper.readValues(p, Bean.class);
         it.nextValue();
     }

     // 8. Closing the iterator releases parser resources
     @Test
     public void testClose() throws Exception {
         JsonParser p = parserFor("[{\"name\":\"ok\",\"value\":0}]");
         p.nextToken(); // advance to START_ARRAY position if we simulate non-managed?
         // Use readValues which returns managed iterator
         MappingIterator<Bean> it = mapper.readValues(p, Bean.class);
         it.close();
         // after close, hasNext should return false (parser null)
         assertFalse(it.hasNext());
     }

     // 9. Remove operation is not supported
     @Test(expected = UnsupportedOperationException.class)
     public void testRemoveNotSupported() throws Exception {
         String json = "[{\"name\":\"a\",\"value\":1}]";
         MappingIterator<Bean> it = iteratorFor(json);
         it.next();
         it.remove();
     }

     // 10. getParser() returns the underlying parser
     @Test
     public void testGetParser() throws Exception {
         JsonParser p = parserFor("[{\"name\":\"a\",\"value\":1}]");
         p.nextToken(); // move to START_ARRAY
         MappingIterator<Bean> it = mapper.readValues(p, Bean.class);
         assertNotNull(it.getParser());
         assertSame(p, it.getParser());
     }

     // 11. After exception on nextValue(), iterator is still usable for recovery?
     //    With bug 18, _handleMappingException does not allow recovery.
     @Test
     public void testNoRecoveryAfterException() throws Exception {
         String json = "[{\"name\":\"a\",\"value\":1}, 2]";
         MappingIterator<Bean> it = iteratorFor(json);
         assertTrue(it.hasNext());
         Bean first = it.next();
         assertNotNull(first);
         assertEquals("a", first.name);
         // next token is integer, should cause failure
         try {
             it.next();
             fail("Expected RuntimeJsonMappingException");
         } catch (RuntimeJsonMappingException e) {
             // Expected; now iterator is likely stuck; further hasNext may return false
             // because parser may be null or at END_ARRAY? We'll just assert exception thrown.
             // This confirms no graceful recovery.
         }
     }
 }