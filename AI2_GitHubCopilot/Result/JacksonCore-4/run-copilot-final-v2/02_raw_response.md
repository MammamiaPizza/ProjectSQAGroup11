package com.fasterxml.jackson.core.util;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;

public class TestTextBuffer {

 private TextBuffer textBuffer;

 @Before
 public void setUp() {
     // Use null allocator to avoid dependency on BufferRecycler constructor specifics
     textBuffer = new TextBuffer(null);
 }

 /**
  * Reproduces the reported bug: expanding the current segment when its length is
  * exactly MAX_SEGMENT_LEN (262144) should increase the length, but the buggy
  * implementation left it unchanged.
  */
 @Test
 public void testExpandAtMaxSegmentLength() {
     char[] seg = textBuffer.getCurrentSegment();
     // Fill internal size to match current segment capacity (optional)
     textBuffer.setCurrentLength(seg.length);
     // Grow as much as possible without reaching the maximum
     while (seg.length < 262144) {
         seg = textBuffer.expandCurrentSegment();
     }
     assertEquals("Precondition: segment length should be MAX_SEGMENT_LEN", 262144, seg.length);
     int oldLen = seg.length;
     seg = textBuffer.expandCurrentSegment();
     assertTrue("Expected buffer of " + oldLen + " to expand, did not, length now " + seg.length,
             seg.length > oldLen);
 }

 /**
  * The no-arg expandCurrentSegment should increase the buffer size by at least 50%.
  */
 @Test
 public void testExpandCurrentSegmentGrowsByHalf() {
     char[] seg = textBuffer.getCurrentSegment();
     int initial = seg.length;
     seg = textBuffer.expandCurrentSegment();
     assertTrue("Grew by less than 50%: " + initial + " -> " + seg.length,
             seg.length >= initial + (initial >> 1));
 }

 /**
  * The parameterized expandCurrentSegment(int minSize) must allocate at least the
  * requested minimum when the current buffer is too small.
  */
 @Test
 public void testExpandCurrentSegmentMinSize() {
     char[] seg = textBuffer.getCurrentSegment();
     int minSize = seg.length + 5000;
     seg = textBuffer.expandCurrentSegment(minSize);
     assertTrue("Expanded length " + seg.length + " less than requested minSize " + minSize,
             seg.length >= minSize);
 }

 /**
  * When the current buffer is already large enough, expandCurrentSegment(minSize)
  * should return the exact same array instance.
  */
 @Test
 public void testExpandCurrentSegmentMinSizeNoChange() {
     char[] seg = textBuffer.getCurrentSegment();
     int minSize = Math.max(1, seg.length / 2);
     char[] result = textBuffer.expandCurrentSegment(minSize);
     assertSame("Should return the same buffer when already large enough", seg, result);
     assertEquals("Length should stay unchanged", seg.length, result.length);
 }

 /**
  * The contents of the buffer should be preserved after an in-place expansion.
  */
 @Test
 public void testExpandCurrentSegmentPreservesContent() {
     char[] seg = textBuffer.getCurrentSegment();
     textBuffer.setCurrentLength(3);
     seg[0] = 'A';
     seg[1] = 'B';
     seg[2] = 'C';
     seg = textBuffer.expandCurrentSegment();
     assertEquals("First character lost", 'A', seg[0]);
     assertEquals("Second character lost", 'B', seg[1]);
     assertEquals("Third character lost", 'C', seg[2]);
 }

 /**
  * After resetting with a shared buffer, the first call to getCurrentSegment
  * must unshare and return a separate array; expansion subsequently must still
  * contain the original content.
  */
 @Test
 public void testExpandAfterUnshareFromShared() {
     char[] shared = new char[] { 'x', 'y', 'z' };
     textBuffer.resetWithShared(shared, 0, 3);
     char[] first = textBuffer.getCurrentSegment();
     assertNotSame("Should unshare from original array", shared, first);
     assertEquals("Unshared content mismatch", 'x', first[0]);
     assertTrue("Unshared buffer too small (need room for at least original content)",
             first.length >= 3);
     // Now expand and check that content is still there
     char[] expanded = textBuffer.expandCurrentSegment(first.length + 100);
     assertEquals("Content lost after expansion", 'x', expanded[0]);
     assertEquals("Content lost after expansion", 'z', expanded[2]);
 }

 /**
  * Appending more data than fits into the current segment forces creation
  * of at least one additional segment (via private expand(int)).
  * The buffer size must reflect the total appended length.
  */
 @Test
 public void testAppendCreatesMultipleSegments() {
     textBuffer.resetWithEmpty();
     textBuffer.getCurrentSegment(); // ensure segment exists
     int total = textBuffer.size(); // 0
     // Fill current segment to capacity and force a new segment
     char[] chunk = new char[2000];
     Arrays.fill(chunk, 'q');
     textBuffer.append(chunk, 0, chunk.length);
     total += chunk.length;
     assertEquals("Total size should match appended amount", total, textBuffer.size());
     // Append more to confirm multiple segments work
     textBuffer.append(chunk, 0, chunk.length);
     total += chunk.length;
     assertEquals("Size after second append", total, textBuffer.size());
 }

 /**
  * Expansion when the segment length is just below MAX_SEGMENT_LEN should
  * cap at MAX_SEGMENT_LEN (no overshoot in the no-arg path).
  */
 @Test
 public void testExpandNearMaxSegmentCap() {
     char[] seg = textBuffer.getCurrentSegment();
     // Expand efficiently towards MAX_SEGMENT_LEN - 1
     do {
         seg = textBuffer.expandCurrentSegment();
     } while (seg.length < 262143);

     seg = textBuffer.expandCurrentSegment();
     // With a length of 262143, 50% growth would be 393214, but it should be capped at 262144
     assertEquals("Expansion should cap at MAX_SEGMENT_LEN (262144)", 262144, seg.length);
 }

 /**
  * Using expandCurrentSegment(minSize) with a value larger than MAX_SEGMENT_LEN
  * must return an array of at least the requested size (no artificial cap).
  */
 @Test
 public void testExpandMinSizeAboveMaxSegment() {
     char[] seg = textBuffer.getCurrentSegment();
     int hugeMin = 300000;
     seg = textBuffer.expandCurrentSegment(hugeMin);
     assertTrue("Requested expansion to " + hugeMin + " but got " + seg.length,
             seg.length >= hugeMin);
 }

 /**
  * Calling expandCurrentSegment() on an empty buffer without any prior content
  * behaves safely and returns a new larger array.
  */
 @Test
 public void testExpandOnEmptyBuffer() {
     textBuffer.resetWithEmpty();
     char[] seg = textBuffer.getCurrentSegment();
     assertNotNull("Should have a segment after reset", seg);
     int initial = seg.length;
     seg = textBuffer.expandCurrentSegment();
     assertTrue("Empty buffer should grow", seg.length > initial);
 }

 /**
  * After expansion, getCurrentSegment() must return the same new
  * array as the one returned by expandCurrentSegment.
  */
 @Test
 public void testGetCurrentSegmentAfterExpansion() {
     char[] fresh = textBuffer.getCurrentSegment();
     char[] expanded = textBuffer.expandCurrentSegment();
     assertSame("getCurrentSegment() should return the expanded array",
             expanded, textBuffer.getCurrentSegment());
 }

}