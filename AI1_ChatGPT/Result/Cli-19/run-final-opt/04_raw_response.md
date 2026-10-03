public void testLongOptionWithEqualsSeparatesOptionAndValue() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("f", "file", true, "file value");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "--file=value" });

    assertTrue(commandLine.hasOption("f"));
    assertEquals("value", commandLine.getOptionValue("f"));
    assertEquals(0, commandLine.getArgs().length);
}

public void testSingleHyphenAndPlainArgumentArePreserved() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "-", "plain" });

    assertEquals(2, commandLine.getArgs().length);
    assertEquals("-", commandLine.getArgs()[0]);
    assertEquals("plain", commandLine.getArgs()[1]);
}

public void testOptionArgumentIsConsumedAsItsValue() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("f", true, "file");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "-f", "value" });

    assertTrue(commandLine.hasOption("f"));
    assertEquals("value", commandLine.getOptionValue("f"));
    assertEquals(0, commandLine.getArgs().length);
}

public void testStopAtNonOptionPreservesUnknownOptionAfterBurst() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("a", false, "accepted");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            options, new String[] { "-az", "tail" }, true);

    assertTrue(commandLine.hasOption("a"));
    assertEquals(2, commandLine.getArgs().length);
    assertEquals("-z", commandLine.getArgs()[0]);
    assertEquals("tail", commandLine.getArgs()[1]);
}