public void testDoubleDashConsumesRemainingArgumentsWithoutAddingDelimiters() throws Exception
{
    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            new org.apache.commons.cli.Options(),
            new String[] { "--", "first", "--", "second" });

    assertEquals(2, commandLine.getArgs().length);
    assertEquals("first", commandLine.getArgs()[0]);
    assertEquals("second", commandLine.getArgs()[1]);
}

public void testLoneDashIsAnArgumentUnlessStopAtNonOptionIsEnabled() throws Exception
{
    org.apache.commons.cli.CommandLine normalCommandLine =
        new org.apache.commons.cli.PosixParser().parse(
            new org.apache.commons.cli.Options(),
            new String[] { "-", "value" },
            false);

    assertEquals(2, normalCommandLine.getArgs().length);
    assertEquals("-", normalCommandLine.getArgs()[0]);
    assertEquals("value", normalCommandLine.getArgs()[1]);

    org.apache.commons.cli.CommandLine stoppedCommandLine =
        new org.apache.commons.cli.PosixParser().parse(
            new org.apache.commons.cli.Options(),
            new String[] { "-", "value" },
            true);

    assertEquals(1, stoppedCommandLine.getArgs().length);
    assertEquals("value", stoppedCommandLine.getArgs()[0]);
}

public void testStopAtNonOptionLeavesFollowingRecognizedOptionAsArgument() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("f", true, "file");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options,
            new String[] { "input", "-f", "output" },
            true);

    assertFalse(commandLine.hasOption("f"));
    assertEquals(3, commandLine.getArgs().length);
    assertEquals("input", commandLine.getArgs()[0]);
    assertEquals("-f", commandLine.getArgs()[1]);
    assertEquals("output", commandLine.getArgs()[2]);
}

public void testOptionalArgumentDoesNotConsumeFollowingOption() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.Option optionalArgument =
        new org.apache.commons.cli.Option("a", true, "optional argument");
    optionalArgument.setOptionalArg(true);
    options.addOption(optionalArgument);
    options.addOption("b", false, "flag");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options,
            new String[] { "-a", "-b" });

    assertTrue(commandLine.hasOption("a"));
    assertNull(commandLine.getOptionValue("a"));
    assertTrue(commandLine.hasOption("b"));
}