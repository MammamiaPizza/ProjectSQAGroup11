public void testProcessArgsLeavesFollowingRecognizedOptionForParser() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("a", true, "argument"));
    options.addOption(new org.apache.commons.cli.Option("b", false, "flag"));

    org.apache.commons.cli.CommandLine commandLine = createIdentityParser().parse(
        options, new String[] { "-a", "value", "-b" });

    assertEquals("value", commandLine.getOptionValue("a"));
    assertTrue(commandLine.hasOption("b"));
}

public void testStopAtNonOptionRetainsUnknownOptionAndRemainingArguments() throws Exception
{
    org.apache.commons.cli.CommandLine commandLine = createIdentityParser().parse(
        new org.apache.commons.cli.Options(),
        new String[] { "-unknown", "value" },
        true);

    assertEquals(2, commandLine.getArgs().length);
    assertEquals("-unknown", commandLine.getArgs()[0]);
    assertEquals("value", commandLine.getArgs()[1]);
}

public void testDoubleDashStopsParsingAndIsNotRepeatedInArguments() throws Exception
{
    org.apache.commons.cli.CommandLine commandLine = createIdentityParser().parse(
        new org.apache.commons.cli.Options(),
        new String[] { "--", "--", "value" });

    assertEquals(1, commandLine.getArgs().length);
    assertEquals("value", commandLine.getArgs()[0]);
}

private org.apache.commons.cli.Parser createIdentityParser()
{
    return new org.apache.commons.cli.Parser()
    {
        protected String[] flatten(org.apache.commons.cli.Options options,
                                   String[] arguments,
                                   boolean stopAtNonOption)
        {
            return arguments;
        }
    };
}