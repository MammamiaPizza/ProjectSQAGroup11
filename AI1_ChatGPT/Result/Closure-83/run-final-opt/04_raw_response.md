@Test
public void explicitTrueVersionValuePrintsVersionAndPreventsCompilation() {
  java.io.ByteArrayOutputStream outputBytes = new java.io.ByteArrayOutputStream();
  java.io.ByteArrayOutputStream errorBytes = new java.io.ByteArrayOutputStream();
  CommandLineRunner runner = new CommandLineRunner(
      new String[] {"--version=true"},
      new java.io.PrintStream(outputBytes),
      new java.io.PrintStream(errorBytes));

  assertFalse(runner.shouldRunCompiler());
  assertTrue(outputBytes.toString().contains("Closure Compiler"));
}

@Test
public void helpFlagPreventsCompilation() {
  java.io.ByteArrayOutputStream outputBytes = new java.io.ByteArrayOutputStream();
  java.io.ByteArrayOutputStream errorBytes = new java.io.ByteArrayOutputStream();
  CommandLineRunner runner = new CommandLineRunner(
      new String[] {"--help"},
      new java.io.PrintStream(outputBytes),
      new java.io.PrintStream(errorBytes));

  assertFalse(runner.shouldRunCompiler());
}

@Test
public void defaultExternArchiveContainsExternSources() throws java.io.IOException {
  assertFalse(CommandLineRunner.getDefaultExterns().isEmpty());
}