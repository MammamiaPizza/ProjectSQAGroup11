@Test
public void shouldRejectMissingRequiredOption() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.Option required = new org.apache.commons.cli.Option("r", false, "required");
    required.setRequired(true);
    options.addOption(required);

    try
    {
        new org.apache.commons.cli.DefaultParser().parse(options, new String[0]);
        fail("A missing required option must be rejected");
    }
    catch (org.apache.commons.cli.MissingOptionException expected)
    {
        assertNotNull(expected);
    }
}

@Test
public void shouldParseLongOptionWithEqualsArgument() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("o", "output", true, "output"));

    org.apache.commons.cli.CommandLine commandLine =
            new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--output=result.txt" });

    assertTrue(commandLine.hasOption("output"));
    assertEquals("result.txt", commandLine.getOptionValue("output"));
}

@Test
public void shouldRejectAmbiguousLongOptionPrefix() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("f", "first", false, "first"));
    options.addOption(new org.apache.commons.cli.Option("F", "force", false, "force"));

    try
    {
        new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--fo" });
        fail("An ambiguous long option prefix must be rejected");
    }
    catch (org.apache.commons.cli.AmbiguousOptionException expected)
    {
        assertNotNull(expected);
    }
}

@Test
public void shouldParseAttachedShortOptionArgument() throws Exception
{
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("f", true, "file"));

    org.apache.commons.cli.CommandLine commandLine =
            new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "-finput.txt" });

    assertTrue(commandLine.hasOption("f"));
    assertEquals("input.txt", commandLine.getOptionValue("f"));
}