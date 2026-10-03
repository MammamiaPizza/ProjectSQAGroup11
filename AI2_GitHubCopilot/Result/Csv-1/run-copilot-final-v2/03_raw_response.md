package org.apache.commons.csv;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

public class ExtendedBufferedReaderTest {

 private ExtendedBufferedReader createReader(String input) {
     return new ExtendedBufferedReader(new StringReader(input));
 }

 /**
  * Trigger bug: CR-terminated line via read() should count as one line.
  * The buggy implementation ignores \r in read(), so line number stays 0.
  */
 @Test
 public void testReadCRIncrementsLineNumber() throws IOException {
     ExtendedBufferedReader br = createReader("a\r");
     while (br.read() != -1) {}
     assertEquals("CR should terminate a line", 1, br.getLineNumber());
 }

 @Test
 public void testReadLFIncrementsLineNumber() throws IOException {
     ExtendedBufferedReader br = createReader("a\n");
     while (br.read() != -1) {}
     assertEquals(1, br.getLineNumber());
 }

 @Test
 public void testReadCRLFCountsOneLine() throws IOException {
     ExtendedBufferedReader br = createReader("a\r\n");
     while (br.read() != -1) {}
     assertEquals("CRLF should be one line terminator", 1, br.getLineNumber());
 }

 @Test
 public void testEmptyInputLineNumber() throws IOException {
     ExtendedBufferedReader br = createReader("");
     assertEquals(-1, br.read());
     assertEquals(0, br.getLineNumber());
 }

 @Test
 public void testReadLineWithCR() throws IOException {
     ExtendedBufferedReader br = createReader("first\rsecond\r");
     assertEquals("first", br.readLine());
     assertEquals(1, br.getLineNumber());
     assertEquals("second", br.readLine());
     assertEquals(2, br.getLineNumber());
     assertNull(br.readLine());
     assertEquals(2, br.getLineNumber());
 }

 @Test
 public void testReadLineMixedEndings() throws IOException {
     ExtendedBufferedReader br = createReader("line1\r\nline2\nline3\rline4");
     assertEquals("line1", br.readLine());
     assertEquals("line2", br.readLine());
     assertEquals("line3", br.readLine());
     assertEquals("line4", br.readLine());
     assertNull(br.readLine());
     assertEquals(4, br.getLineNumber());
 }

 @Test
 public void testReadLineUnterminatedEOF() throws IOException {
     ExtendedBufferedReader br = createReader("no_newline");
     assertEquals("no_newline", br.readLine());
     assertEquals(1, br.getLineNumber());
     assertNull(br.readLine());
     assertEquals(1, br.getLineNumber());
 }

 @Test
 public void testReadCharArrayCRLFOneLine() throws IOException {
     ExtendedBufferedReader br = createReader("ab\r\ncd");
     char[] buf = new char[10];
     int len = br.read(buf, 0, 10);
     assertEquals(6, len);
     assertEquals("CRLF should count as one line", 1, br.getLineNumber());
 }

 @Test
 public void testReadCharArrayCR() throws IOException {
     ExtendedBufferedReader br = createReader("x\ry");
     char[] buf = new char[10];
     int len = br.read(buf, 0, 10);
     assertEquals(3, len);
     assertEquals(1, br.getLineNumber());
 }

 @Test
 public void testLookAheadDoesNotAffectState() throws IOException {
     ExtendedBufferedReader br = createReader("abc\r\n");
     assertEquals('a', br.lookAhead());
     assertEquals(0, br.getLineNumber());
     assertEquals('a', br.read());
     assertEquals(0, br.getLineNumber());
     while (br.read() != -1) {}
     assertEquals(1, br.getLineNumber());
 }

 @Test
 public void testLastCharTracking() throws IOException {
     ExtendedBufferedReader br = createReader("abc");
     assertEquals('a', br.read());
     assertEquals('a', br.readAgain());
     assertEquals('b', br.read());
     assertEquals('b', br.readAgain());
     assertEquals('c', br.read());
     assertEquals('c', br.readAgain());
     assertEquals(-1, br.read());
     assertEquals(-1, br.readAgain());
 }

 @Test
 public void testConsecutiveBlankLines() throws IOException {
     ExtendedBufferedReader br = createReader("\n\n\n");
     assertEquals("", br.readLine());
     assertEquals(1, br.getLineNumber());
     assertEquals("", br.readLine());
     assertEquals(2, br.getLineNumber());
     assertEquals("", br.readLine());
     assertEquals(3, br.getLineNumber());
     assertNull(br.readLine());
     assertEquals(3, br.getLineNumber());
 }

}