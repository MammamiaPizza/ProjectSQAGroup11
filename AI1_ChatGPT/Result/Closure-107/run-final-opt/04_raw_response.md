@Test
public void testDefaultConstructorAcceptsEmptyArguments() {
  CommandLineRunner runner = new CommandLineRunner(new String[0]);

  assertTrue(runner.shouldRunCompiler());
}

@Test
public void testUseOnlyCustomExternsWithNoCustomExternsReturnsEmptyList()
    throws Exception {
  CommandLineRunner runner = new CommandLineRunner(
      new String[] {"--use_only_custom_externs"},
      new java.io.PrintStream(new java.io.ByteArrayOutputStream()),
      new java.io.PrintStream(new java.io.ByteArrayOutputStream()));

  assertEquals(0, runner.createExterns().size());
}

@Test
public void testBundledDefaultExternsAreAvailable() throws Exception {
  java.util.List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

  assertFalse(externs.isEmpty());
}