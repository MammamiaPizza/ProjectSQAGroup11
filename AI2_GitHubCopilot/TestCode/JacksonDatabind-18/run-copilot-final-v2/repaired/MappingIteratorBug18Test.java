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
     // Must consume START_ARRAY if present, because MappingIterator with managed parser does that
     if (p.nextToken() == JsonToken.START_ARRAY) {
         // Managed parser case: clear so iterator works
     } else {
         throw new IOException("Expected JSON array");
     }
     return mapper.readValues(p, Bean.class);
 }

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

 @Test(expected = JsonMappingException.class)
 public void testNextValueThrowsOnInvalidToken() throws Exception {
     String json = "[{\"name\":\"a\",\"value\":1}, 1, {\"name\":\"c\",\"value\":3}]";
     MappingIterator<Bean> it = iteratorFor(json);
     it.nextValue();
     it.nextValue();
 }

 @Test
 public void testReadAllPartialOnError() throws Exception {
     String json = "[{\"name\":\"a\",\"value\":1}, 1, {\"name\":\"c\",\"value\":3}]";
     MappingIterator<Bean> it = iteratorFor(json);
     List<Bean> beans = new ArrayList<Bean>();
     try {
         it.readAll(beans);
         fail("Should have thrown an exception because of invalid token");
     } catch (JsonMappingException e) {
         assertEquals(1, beans.size());
         assertEquals("a", beans.get(0).name);
     }
 }

 @Test(expected = RuntimeJsonMappingException.class)
 public void testIterateWithInvalidTokenViaNext() throws Exception {
     String json = "[{\"name\":\"a\",\"value\":1}, 1]";
     MappingIterator<Bean> it = iteratorFor(json);
     assertTrue(it.hasNext());
     assertEquals("a", it.next().name);
     assertTrue(it.hasNext());
     it.next();
 }

 @Test
 public void testConsecutiveErrors() throws Exception {
     String json = "[1, 2, {\"name\":\"x\",\"value\":9}]";
     MappingIterator<Bean> it = iteratorFor(json);
     try {
         it.readAll();
         fail("Expected exception");
     } catch (JsonMappingException e) {
         assertTrue(true);
     }
 }

 @Test(expected = java.util.NoSuchElementException.class)
 public void testEmptyArray() throws Exception {
     String json = "[]";
     MappingIterator<Bean> it = iteratorFor(json);
     assertFalse(it.hasNext());
     it.nextValue();
 }

 @Test(expected = JsonMappingException.class)
 public void testRootLevelInteger() throws Exception {
     JsonParser p = mapper.getFactory().createParser("1");
     MappingIterator<Bean> it = mapper.readValues(p, Bean.class);
     it.nextValue();
 }

 @Test
 public void testClose() throws Exception {
     JsonParser p = parserFor("[{\"name\":\"ok\",\"value\":0}]");
     p.nextToken();
     MappingIterator<Bean> it = mapper.readValues(p, Bean.class);
     it.close();
     assertFalse(it.hasNext());
 }

 @Test(expected = UnsupportedOperationException.class)
 public void testRemoveNotSupported() throws Exception {
     String json = "[{\"name\":\"a\",\"value\":1}]";
     MappingIterator<Bean> it = iteratorFor(json);
     it.next();
     it.remove();
 }

 @Test
 public void testGetParser() throws Exception {
     JsonParser p = parserFor("[{\"name\":\"a\",\"value\":1}]");
     p.nextToken();
     MappingIterator<Bean> it = mapper.readValues(p, Bean.class);
     assertNotNull(it.getParser());
     assertSame(p, it.getParser());
 }

 @Test
 public void testNoRecoveryAfterException() throws Exception {
     String json = "[{\"name\":\"a\",\"value\":1}, 2]";
     MappingIterator<Bean> it = iteratorFor(json);
     assertTrue(it.hasNext());
     Bean first = it.next();
     assertNotNull(first);
     assertEquals("a", first.name);
     try {
         it.next();
         fail("Expected RuntimeJsonMappingException");
     } catch (RuntimeJsonMappingException e) {
     }
 }

}
