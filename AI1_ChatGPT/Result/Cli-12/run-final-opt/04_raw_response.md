public void testDoubleDashPreservesFollowingOptionLikeArguments() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("f", "flag", false, "flag");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.GnuParser().parse(
            options, new String[] { "--", "-f", "value" });

    assertFalse(commandLine.hasOption("f"));
    assertEquals(2, commandLine.getArgs().length);
    assertEquals("-f", commandLine.getArgs()[0]);
    assertEquals("value", commandLine.getArgs()[1]);
}

public void testSingleDashIsRetainedAsArgument() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("f", "flag", false, "flag");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.GnuParser().parse(
            options, new String[] { "-", "-f" });

    assertTrue(commandLine.hasOption("f"));
    assertEquals(1, commandLine.getArgs().length);
    assertEquals("-", commandLine.getArgs()[0]);
}

public void testNonOptionDoesNotPreventLaterOptionByDefault() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("f", "flag", false, "flag");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.GnuParser().parse(
            options, new String[] { "input.txt", "-f" });

    assertTrue(commandLine.hasOption("f"));
    assertEquals(1, commandLine.getArgs().length);
    assertEquals("input.txt", commandLine.getArgs()[0]);
}

public void testShortOptionCanConsumeAttachedPropertyValue() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("D", "define", true, "property");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.GnuParser().parse(
            options, new String[] { "-Dproperty=value" });

    assertEquals("property=value", commandLine.getOptionValue("D"));
}