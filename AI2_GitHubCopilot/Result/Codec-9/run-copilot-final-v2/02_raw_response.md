package org.apache.commons.codec.binary;

import static org.junit.Assert.*;
import org.junit.Test;

public class Base64Test {

 @Test
 public void testNonChunkedMaxEqualsEncodedLength() {
     checkNoThrow(false, false, 0);
     checkNoThrow(false, false, 1);
     checkNoThrow(false, false, 2);
     checkNoThrow(false, false, 3);
     checkNoThrow(false, false, 4);
     checkNoThrow(false, false, 999);
 }

 @Test
 public void testNonChunkedMaxLessThanEncodedLengthThrows() {
     checkThrow(false, false, 1);
     checkThrow(false, false, 2);
     checkThrow(false, false, 3);
     checkThrow(false, false, 4);
 }

 @Test
 public void testUrlSafeNonChunkedMaxEqualsEncodedLength() {
     checkNoThrow(false, true, 1);
     checkNoThrow(false, true, 2);
     checkNoThrow(false, true, 4);
 }

 @Test
 public void testUrlSafeNonChunkedMaxLessThrows() {
     checkThrow(false, true, 1);
     checkThrow(false, true, 2);
 }

 @Test
 public void testChunkedMaxEqualsEncodedLength() {
     checkNoThrow(true, false, 0);
     checkNoThrow(true, false, 1);
     checkNoThrow(true, false, 2);
     checkNoThrow(true, false, 76);
     checkNoThrow(true, false, 200);
 }

 @Test
 public void testChunkedMaxLessThrows() {
     checkThrow(true, false, 1);
     checkThrow(true, false, 2);
 }

 @Test
 public void testEmptyInputReturnsEmpty() {
     assertArrayEquals(new byte[0], Base64.encodeBase64(new byte[0], false, false, 0));
     assertArrayEquals(new byte[0], Base64.encodeBase64(new byte[0], false, false, 100));
 }

 @Test
 public void testNullInputReturnsNull() {
     assertNull(Base64.encodeBase64(null, false, false, 0));
     assertNull(Base64.encodeBase64(null, true, false, 10));
 }

 @Test
 public void testLargeInputWithMaxIntegerMaxValue() {
     byte[] data = new byte[10000];
     byte[] result = Base64.encodeBase64(data, false, false, Integer.MAX_VALUE);
     assertNotNull(result);
     assertArrayEquals(Base64.encodeBase64(data), result);
 }

 @Test
 public void testMaxSizeZeroThrowsForNonEmpty() {
     try {
         Base64.encodeBase64(new byte[]{1}, false, false, 0);
         fail("Expected IllegalArgumentException");
     } catch (IllegalArgumentException expected) {
     }
 }

 private void checkNoThrow(boolean chunked, boolean urlSafe, int dataLen) {
     byte[] data = new byte[dataLen];
     for (int i = 0; i < dataLen; i++) {
         data[i] = (byte) (i % 128);
     }
     byte[] expected = chunked ? Base64.encodeBase64Chunked(data)
             : urlSafe ? Base64.encodeBase64URLSafe(data) : Base64.encodeBase64(data);
     int max = expected.length;
     byte[] actual = Base64.encodeBase64(data, chunked, urlSafe, max);
     assertArrayEquals("Failed for len=" + dataLen + " chunked=" + chunked + " urlSafe=" + urlSafe,
             expected, actual);
 }

 private void checkThrow(boolean chunked, boolean urlSafe, int dataLen) {
     byte[] data = new byte[dataLen];
     for (int i = 0; i < dataLen; i++) {
         data[i] = (byte) (i % 128);
     }
     byte[] expected = chunked ? Base64.encodeBase64Chunked(data)
             : urlSafe ? Base64.encodeBase64URLSafe(data) : Base64.encodeBase64(data);
     try {
         Base64.encodeBase64(data, chunked, urlSafe, expected.length - 1);
         fail("Expected IllegalArgumentException for len=" + dataLen);
     } catch (IllegalArgumentException e) {
         // expected
     }
 }

}