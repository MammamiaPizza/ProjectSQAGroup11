public void testParserRejectsMissingRequiredOption() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.Option required = new org.apache.commons.cli.Option("r", false, "required");
    required.setRequired(true);
    options.addOption(required);

    try
    {
        new org.apache.commons.cli.GnuParser().parse(options, new String[0]);
        fail("A missing required option must cause a parse failure");
    }
    catch (org.apache.commons.cli.MissingOptionException expected)
    {
    }
}

public void testParserResetsOptionGroupSelectionBetweenParses() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.OptionGroup group = new org.apache.commons.cli.OptionGroup();
    group.addOption(new org.apache.commons.cli.Option("a", false, "first"));
    group.addOption(new org.apache.commons.cli.Option("b", false, "second"));
    options.addOptionGroup(group);

    org.apache.commons.cli.Parser parser = new org.apache.commons.cli.GnuParser();
    parser.parse(options, new String[] { "-a" });
    org.apache.commons.cli.CommandLine commandLine = parser.parse(options, new String[] { "-b" });

    assertTrue(commandLine.hasOption("b"));
    assertFalse(commandLine.hasOption("a"));
}

public void testParserAcceptsNullArgumentsAsEmptyArguments() throws Exception
{
    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.GnuParser().parse(new org.apache.commons.cli.Options(), null);

    assertEquals(0, commandLine.getArgs().length);
}

public void testParserStopsAtNonOptionAndKeepsRemainingTokensAsArguments() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("a", false, "option");

    org.apache.commons.cli.CommandLine commandLine = new org.apache.commons.cli.GnuParser().parse(
        options, new String[] { "operand", "-a", "--", "tail", "--" }, true);

    assertFalse(commandLine.hasOption("a"));
    assertEquals(3, commandLine.getArgs().length);
    assertEquals("operand", commandLine.getArgs()[0]);
    assertEquals("-a", commandLine.getArgs()[1]);
    assertEquals("tail", commandLine.getArgs()[2]);
}