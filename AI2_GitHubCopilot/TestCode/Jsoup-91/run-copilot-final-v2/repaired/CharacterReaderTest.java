package org.jsoup.parser;

import static org.junit.Assert.*;
import org.junit.Test;
import java.io.IOException;
import java.io.Reader;
import org.jsoup.UncheckedIOException;

public class CharacterReaderTest {

 @Test
 public void testUncheckedIOExceptionWrapsIOException() {
     IOException original = new IOException("original");
     UncheckedIOException wrapped = new UncheckedIOException(original);
     assertSame("getCause should return original IOException", original, wrapped.getCause());
     assertSame("ioException should return original IOException", original, wrapped.ioException());
 }

 @Test
 public void testUncheckedIOExceptionIsRuntimeException() {
     UncheckedIOException ex = new UncheckedIOException(new IOException());
     assertTrue("Should be a RuntimeException", ex instanceof RuntimeException);
 }

 @Test
 public void testBufferUpThrowsUncheckedIOExceptionOnReadError() {
     Reader failingReader = new Reader() {
         boolean readCalled = false;
         @Override
         public int read(char[] cbuf, int off, int len) throws IOException {
             readCalled = true;
             throw new IOException("simulated binary read error");
         }
         @Override
         public void close() throws IOException {}
         @Override
         public boolean markSupported() { return true; }
     };
     CharacterReader cr = new CharacterReader(failingReader, 1024);
     try {
         cr.current();
         fail("Expected UncheckedIOException");
     } catch (UncheckedIOException e) {
         assertTrue(e.getCause() instanceof IOException);
         assertEquals("simulated binary read error", e.getCause().getMessage());
     }
 }

 @Test
 public void testEmptyInput() {
     CharacterReader cr = new CharacterReader("");
     assertTrue(cr.isEmpty());
     assertEquals(CharacterReader.EOF, cr.current());
 }

 @Test(expected = IllegalArgumentException.class)
 public void testNullReaderThrowsIllegalArgumentException() {
     new CharacterReader((Reader) null);
 }

 @Test
 public void testValidTextCurrentAndAdvance() {
     CharacterReader cr = new CharacterReader("Hello");
     assertFalse(cr.isEmpty());
     assertEquals('H', cr.current());
     cr.advance();
     assertEquals('e', cr.current());
     cr.advance();
     assertEquals('l', cr.current());
 }

 @Test
 public void testRangeEqualsFromStartOfBuffer() {
     CharacterReader cr = new CharacterReader("abcdef");
     assertTrue(cr.rangeEquals(0, 3, "abc"));
     assertTrue(cr.rangeEquals(0, 6, "abcdef"));
     assertFalse(cr.rangeEquals(0, 6, "abcdeg"));
 }

 @Test
 public void testRangeEqualsAfterAdvance() {
     CharacterReader cr = new CharacterReader("abcdef");
     cr.advance(); cr.advance(); // positions at 'c'
     assertTrue(cr.rangeEquals(0, 2, "cd"));
     assertTrue(cr.rangeEquals(0, 4, "cdef"));
 }

 @Test
 public void testRangeEqualsMismatch() {
     CharacterReader cr = new CharacterReader("abc");
     assertFalse(cr.rangeEquals(0, 2, "abX"));
     assertFalse(cr.rangeEquals(0, 3, "ABCD"));
 }

 @Test
 public void testRangeEqualsWithCountExceedingBuffer() {
     CharacterReader cr = new CharacterReader("abc");
     assertFalse(cr.rangeEquals(0, 5, "abc??"));
 }

 @Test
 public void testConsumeToAndPos() {
     CharacterReader cr = new CharacterReader("hello world");
     assertEquals(0, cr.pos());
     String part = cr.consumeTo(' ');
     assertEquals("hello", part);
     assertEquals(6, cr.pos()); // after space
     cr.advance(); // skip space
     String rest = cr.consumeTo('d');
     assertEquals("worl", rest);
     assertEquals(11, cr.pos());
 }

 @Test
 public void testBuffferUpReFillsFromLargeReader() throws IOException {
     final String longString = generateString(2048, 'a') + "END";
     Reader largeReader = new java.io.StringReader(longString);
     CharacterReader cr = new CharacterReader(largeReader, 100);
     for (int i = 0; i < 2048; i++) cr.advance();
     assertEquals('E', cr.current());
     cr.advance(); cr.advance(); cr.advance();
     assertEquals(CharacterReader.EOF, cr.current());
 }

 private String generateString(int length, char c) {
     StringBuilder sb = new StringBuilder(length);
     for (int i = 0; i < length; i++) sb.append(c);
     return sb.toString();
 }

}
