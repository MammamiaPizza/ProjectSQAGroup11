@Test
public void testGuessModuleNameHandlesConfiguredPrefixWithTrailingSlashAndUnprefixedFile() {
  ProcessCommonJSModules processor =
      new ProcessCommonJSModules(null, "foo/", false);

  assertEquals("module$bar", processor.guessCJSModuleName("foo/bar.js"));
  assertEquals("module$other$bar", processor.guessCJSModuleName("other/bar.js"));
}

@Test
public void testGuessModuleNameResolvesParentRelativeRequireAgainstCurrentFile() {
  ProcessCommonJSModules processor =
      new ProcessCommonJSModules(null, ".", false);

  assertEquals(
      "module$foo$qux",
      processor.guessCJSModuleName("../qux.js", "foo/bar/baz.js"));
}

@Test
public void testGuessModuleNameWrapsInvalidRelativeRequireUri() {
  ProcessCommonJSModules processor =
      new ProcessCommonJSModules(null, ".", false);

  try {
    processor.guessCJSModuleName("./[", "foo/bar.js");
    throw new AssertionError("Expected invalid relative URI to be rejected");
  } catch (RuntimeException expected) {
    assertEquals(
        java.net.URISyntaxException.class,
        expected.getCause().getClass());
  }
}