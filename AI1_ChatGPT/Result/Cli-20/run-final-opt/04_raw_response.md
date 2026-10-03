public void testLongOptionWithAttachedValueIsParsed()
    throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("D", "define", true, "definition");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "--define=value" }, false);

    assertTrue(commandLine.hasOption("D"));
    assertEquals("value", commandLine.getOptionValue("D"));
    assertEquals(0, commandLine.getArgs().length);
}

public void testSingleHyphenIsAnArgument()
    throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("a", false, "alpha");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "-" }, false);

    assertFalse(commandLine.hasOption("a"));
    assertEquals(1, commandLine.getArgs().length);
    assertEquals("-", commandLine.getArgs()[0]);
}

public void testStopAtNonOptionStopsWithinBurstToken()
    throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("a", false, "alpha");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "-az", "tail" }, true);

    assertTrue(commandLine.hasOption("a"));
    assertEquals(2, commandLine.getArgs().length);
    assertEquals("z", commandLine.getArgs()[0]);
    assertEquals("tail", commandLine.getArgs()[1]);
}