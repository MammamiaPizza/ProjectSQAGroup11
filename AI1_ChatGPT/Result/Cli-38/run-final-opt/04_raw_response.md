@Test
public void parsesLongOptionWithAnEqualsValue() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("o", "output", true, ""));

    org.apache.commons.cli.CommandLine commandLine =
            new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--output=result" });

    org.junit.Assert.assertTrue(commandLine.hasOption("output"));
    org.junit.Assert.assertEquals("result", commandLine.getOptionValue("output"));
}

@Test(expected = org.apache.commons.cli.UnrecognizedOptionException.class)
public void rejectsEqualsValueForLongOptionWithoutArgument() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("v", "verbose", false, ""));

    new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--verbose=true" });
}

@Test(expected = org.apache.commons.cli.MissingOptionException.class)
public void rejectsMissingRequiredOption() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.Option required = new org.apache.commons.cli.Option("r", "required", false, "");
    required.setRequired(true);
    options.addOption(required);

    new org.apache.commons.cli.DefaultParser().parse(options, new String[0]);
}

@Test(expected = org.apache.commons.cli.MissingArgumentException.class)
public void rejectsOptionMissingItsRequiredArgument() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("f", "file", true, ""));

    new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--file" });
}