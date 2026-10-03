package com.fasterxml.jackson.core.util;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class TestTextBuffer {

     private TextBuffer createBuffer() {
         return new TextBuffer(new BufferRecycler());
     }

     @Test
     public void testEmptyContentsAsStringAfterResetWithEmpty() {
         TextBuffer tb = createBuffer();
         tb.resetWithEmpty();
         assertEquals("", tb.contentsAsString());
     }

     @Test
     public void testEmptySizeAfterResetWithEmpty() {
         TextBuffer tb = createBuffer();
         tb.resetWithEmpty();
         assertEquals(0, tb.size());
     }

     @Test
     public void testEmptyGetTextOffsetAfterResetWithEmpty() {
         TextBuffer tb = createBuffer();
         tb.resetWithEmpty();
         assertEquals(0, tb.getTextOffset());
     }

     @Test
     public void testEmptyHasTextAsCharactersAfterResetWithEmpty() {
         TextBuffer tb = createBuffer();
         tb.resetWithEmpty();
         assertFalse(tb.hasTextAsCharacters());
     }

     @Test
     public void testEmptyGetTextBufferAfterResetWithEmpty() {
         TextBuffer tb = createBuffer();
         tb.resetWithEmpty();
         assertNotNull(tb.getTextBuffer());
     }

     @Test
     public void testContentsAsStringAfterAppendSingleChar() {
         TextBuffer tb = createBuffer();
         tb.append('A');
         assertEquals("A", tb.contentsAsString());
     }

     @Test
     public void testContentsAsStringAfterAppendChars() {
         TextBuffer tb = createBuffer();
         tb.append("xyz".toCharArray(), 0, 3);
         assertEquals("xyz", tb.contentsAsString());
     }

     @Test
     public void testResetWithEmptyBeforeAppend() {
         TextBuffer tb = createBuffer();
         tb.append('X');
         assertEquals("X", tb.contentsAsString());
         tb.resetWithEmpty();
         assertEquals("", tb.contentsAsString());
         tb.append('Y');
         assertEquals("Y", tb.contentsAsString());
     }

     @Test
     public void testResetWithSharedNullBuffer() {
         TextBuffer tb = createBuffer();
         try {
             tb.resetWithShared(null, 0, 0);
             // No immediate exception; contents access may fail later but not tested here.
         } catch (NullPointerException ok) {
             // Acceptable if null argument triggers immediate NPE.
         }
     }

     @Test
     public void testResetWithCopyNullBuffer() {
         TextBuffer tb = createBuffer();
         try {
             tb.resetWithCopy(null, 0, 0);
         } catch (NullPointerException ok) {
             // Acceptable immediate rejection.
         }
     }

     @Test
     public void testResetWithStringNull() {
         TextBuffer tb = createBuffer();
         tb.resetWithString(null);
         String s = tb.contentsAsString();
         assertTrue(s == null || s.isEmpty());
     }

     @Test
     public void testResetWithSharedValid() {
         TextBuffer tb = createBuffer();
         char[] arr = "abc".toCharArray();
         tb.resetWithShared(arr, 0, 3);
         assertEquals("abc", tb.contentsAsString());
         assertSame(arr, tb.getTextBuffer());
     }
 }
