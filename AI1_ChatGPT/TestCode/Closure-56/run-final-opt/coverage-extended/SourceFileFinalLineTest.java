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

@org.junit.Test
public void testConstructorRejectsMissingNames() {
  try {
    new SourceFile(null);
    fail("Expected an IllegalArgumentException for a null name");
  } catch (IllegalArgumentException expected) {
  }

  try {
    new SourceFile("");
    fail("Expected an IllegalArgumentException for an empty name");
  } catch (IllegalArgumentException expected) {
  }
}

@org.junit.Test
public void testReaderAndInputStreamFactoriesReadUtf8Source() throws Exception {
  SourceFile fromReader = SourceFile.fromReader(
      "reader.js", new java.io.StringReader("first\nsecond"));
  assertEquals("reader.js", fromReader.getName());
  assertEquals("first\nsecond", fromReader.getCode());

  java.io.Reader codeReader = fromReader.getCodeReader();
  try {
    assertEquals((int) 'f', codeReader.read());
  } finally {
    codeReader.close();
  }

  SourceFile fromStream = SourceFile.fromInputStream(
      "stream.js",
      new java.io.ByteArrayInputStream("café".getBytes("UTF-8")));
  assertEquals("stream.js", fromStream.getName());
  assertEquals("café", fromStream.getCode());
}

@org.junit.Test
public void testGeneratedSourceRegeneratesAfterClearingCachedSource() throws Exception {
  final int[] generations = new int[] {0};
  SourceFile source = SourceFile.fromGenerator("generated.js", new SourceFile.Generator() {
    @Override
    public String getCode() {
      generations[0]++;
      return "generated" + generations[0];
    }
  });

  assertEquals("generated1", source.getCode());
  assertEquals("generated1", source.getCode());
  assertEquals(1, generations[0]);

  source.clearCachedSource();

  assertEquals("generated2", source.getCode());
  assertEquals(2, generations[0]);
}

@org.junit.Test
public void testUnreadableSourceHasNoLineAndUsesFallbackLineOffset() {
  SourceFile source = new SourceFile("unreadable.js") {
    @Override
    public String getCode() throws java.io.IOException {
      throw new java.io.IOException("unreadable");
    }
  };

  assertNull(source.getLine(1));
  assertEquals(1, source.getNumLines());
  assertEquals(0, source.getLineOffset(1));
}
}
