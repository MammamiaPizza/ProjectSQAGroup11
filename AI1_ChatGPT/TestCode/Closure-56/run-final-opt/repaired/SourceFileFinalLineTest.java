package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;

public class SourceFileFinalLineTest {

  @Test
  public void testGetLineReturnsFinalUnterminatedLineAndSupportsEarlierLookups() {
    SourceFile source =
        SourceFile.fromCode("test.js", "foo0:first line\nfoo1:second line\nfoo2:third line");

    assertEquals("foo2:third line", source.getLine(3));
    assertEquals("foo1:second line", source.getLine(2));
    assertEquals("foo0:first line", source.getLine(1));
    assertNull(source.getLine(4));
  }

  @Test
  public void testGetLineReturnsOnlyLineWhenFileHasNoNewline() {
    SourceFile source = SourceFile.fromCode("single.js", "var value = 1;");

    assertEquals("var value = 1;", source.getLine(1));
    assertNull(source.getLine(2));
  }

  @Test
  public void testGetRegionForFinalUnterminatedLineContainsThatLine() {
    SourceFile source =
        SourceFile.fromCode("region.js", "first line\nsecond line\nfinal unterminated line\n");

    Region region = source.getRegion(3);

    assertNotNull(region);
    assertEquals(true, region.getSourceExcerpt().contains("final unterminated line"));
    assertFalse(region.getSourceExcerpt().contains("\r"));
  }

  @Test
  public void testLineOffsetsIncludeFinalUnterminatedLine() {
    SourceFile source = SourceFile.fromCode("offsets.js", "one\ntwo\nthree");

    assertEquals(0, source.getLineOffset(1));
    assertEquals(4, source.getLineOffset(2));
    assertEquals(8, source.getLineOffset(3));

    try {
      source.getLineOffset(4);
      fail("Expected an exception for a line beyond the final unterminated line");
    } catch (IllegalArgumentException expected) {
      assertNotNull(expected.getMessage());
    }
  }
}
