public void testLongOptionArgumentWithAndWithoutEquals() throws Exception
{
    org.apache.commons.cli.Options equalsOptions = new org.apache.commons.cli.Options();
    equalsOptions.addOption(new org.apache.commons.cli.Option("f", "file", true, ""));

    org.apache.commons.cli.CommandLine equalsCommandLine =
        new org.apache.commons.cli.PosixParser().parse(equalsOptions,
            new String[] { "--file=value" });
    assertEquals("value", equalsCommandLine.getOptionValue("file"));

    org.apache.commons.cli.Options separateOptions = new org.apache.commons.cli.Options();
    separateOptions.addOption(new org.apache.commons.cli.Option("f", "file", true, ""));

    org.apache.commons.cli.CommandLine separateCommandLine =
        new org.apache.commons.cli.PosixParser().parse(separateOptions,
            new String[] { "--file", "value" });
    assertEquals("value", separateCommandLine.getOptionValue("file"));
}

public void testBurstOptionWithAttachedArgument() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption("f", true, "");

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(options,
            new String[] { "-fvalue" });

    assertTrue(commandLine.hasOption("f"));
    assertEquals("value", commandLine.getOptionValue("f"));
}

public void testSingleHyphenIsAnArgument() throws Exception
{
    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(
            new org.apache.commons.cli.Options(), new String[] { "-", "value" });

    assertEquals(2, commandLine.getArgs().length);
    assertEquals("-", commandLine.getArgs()[0]);
    assertEquals("value", commandLine.getArgs()[1]);
}

public void testOptionAcceptingMultipleArguments() throws Exception
{
    org.apache.commons.cli.Option option = new org.apache.commons.cli.Option("D", true, "");
    option.setArgs(2);

    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(option);

    org.apache.commons.cli.CommandLine commandLine =
        new org.apache.commons.cli.PosixParser().parse(options,
            new String[] { "-D", "first", "second" });

    assertEquals("first", commandLine.getOptionValues("D")[0]);
    assertEquals("second", commandLine.getOptionValues("D")[1]);
}