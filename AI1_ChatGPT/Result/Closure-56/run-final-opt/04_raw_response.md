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