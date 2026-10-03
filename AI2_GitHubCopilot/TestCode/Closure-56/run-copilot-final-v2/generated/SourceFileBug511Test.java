package com.google.javascript.jscomp;

import static org.junit.Assert.*;

import org.junit.Test;

/**

 - Tests for {@link SourceFile} targeting bug #511: getLine and getRegion
 - return null when the last line has no trailing newline.
  */
 public class SourceFileBug511Test {

  // ---------- getLine ----------

  @Test public void testGetLineLastLineNoNewline() throws Exception {
    // Bug: last line without trailing \n should return its content, not null.
    SourceFile sf = SourceFile.fromCode("foo", "first\nsecond\nthird line");
    assertEquals("third line", sf.getLine(3)); }

  @Test public void testGetLineLastLineWithNewline() throws Exception {
    // Sanity: last line WITH trailing \n should also work correctly.
    SourceFile sf = SourceFile.fromCode("foo", "alpha\nbeta\ngamma\n");
    assertEquals("gamma", sf.getLine(3)); }

  @Test public void testGetLineFirstLine() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "aaaa\nbbbb\ncccc");
    assertEquals("aaaa", sf.getLine(1)); }

  @Test public void testGetLineMiddleLine() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "x\ny\nz\n");
    assertEquals("y", sf.getLine(2)); }

  @Test public void testGetLineOutOfRange() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "one\ntwo");
    assertNull(sf.getLine(5)); }

  @Test public void testGetLineZeroAndNegative() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "a\nb\nc");
    // line numbers are 1-based; 0 and negative should return null.
    assertNull(sf.getLine(0));
    assertNull(sf.getLine(-1)); }

  @Test public void testGetLineAfterSetCode() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "old\ncontent");
    sf.setCode("new\ncode\nwithout newline");
    assertEquals("without newline", sf.getLine(3));
    assertEquals("new", sf.getLine(1)); }

  @Test public void testGetLineEmptyCode() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "");
    assertNull(sf.getLine(1)); }

  @Test public void testGetLineSingleLineNoNewline() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "just one line");
    assertEquals("just one line", sf.getLine(1)); }

  // ---------- getRegion ----------

  @Test public void testGetRegionLastLineNoNewline() throws Exception {
    // The bug also affects getRegion for the last line without trailing \n.
    SourceFile sf = SourceFile.fromCode("foo", "foo1\nfoo2:third line");
    Region region = sf.getRegion(2);
    assertNotNull("Region for last line without newline must not be null", region);
    assertTrue(region.getSourceExcerpt().contains("foo2:third line")); }

  @Test @Test public void testGetRegionOutOfRange() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "a\nb\nc\n");
    assertNull(sf.getRegion(100)); }

  @Test public void testGetRegionFirstLine() throws Exception {
    SourceFile sf = SourceFile.fromCode("foo", "first\nsecond\nthird");
    Region region = sf.getRegion(1);
    assertNotNull(region);
    assertTrue(region.getSourceExcerpt().contains("first")); }
}
