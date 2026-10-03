public void testPrintUsageFormatsOptionalAndRequiredOptionGroups()
{
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    org.apache.commons.cli.Options optionalOptions = new org.apache.commons.cli.Options();
    org.apache.commons.cli.OptionGroup optionalGroup = new org.apache.commons.cli.OptionGroup();
    optionalGroup.addOption(new org.apache.commons.cli.Option("b", false, "beta"));
    optionalGroup.addOption(new org.apache.commons.cli.Option("a", false, "alpha"));
    optionalOptions.addOptionGroup(optionalGroup);

    java.io.StringWriter optionalOutput = new java.io.StringWriter();
    formatter.printUsage(new java.io.PrintWriter(optionalOutput), 80, "app", optionalOptions);

    assertEquals("usage: app [-a | -b]" + formatter.getNewLine(), optionalOutput.toString());

    org.apache.commons.cli.Options requiredOptions = new org.apache.commons.cli.Options();
    org.apache.commons.cli.OptionGroup requiredGroup = new org.apache.commons.cli.OptionGroup();
    requiredGroup.setRequired(true);
    requiredGroup.addOption(new org.apache.commons.cli.Option("b", false, "beta"));
    requiredGroup.addOption(new org.apache.commons.cli.Option("a", false, "alpha"));
    requiredOptions.addOptionGroup(requiredGroup);

    java.io.StringWriter requiredOutput = new java.io.StringWriter();
    formatter.printUsage(new java.io.PrintWriter(requiredOutput), 80, "app", requiredOptions);

    assertEquals("usage: app -a | -b" + formatter.getNewLine(), requiredOutput.toString());
}

public void testPrintUsageUsesLongOptionSeparatorForLongOnlyArgument()
{
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    formatter.setLongOptSeparator("=");
    formatter.setArgName("path");

    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option(null, "file", true, "file path"));

    java.io.StringWriter output = new java.io.StringWriter();
    formatter.printUsage(new java.io.PrintWriter(output), 80, "app", options);

    assertEquals("usage: app [--file=<path>]" + formatter.getNewLine(), output.toString());
}

public void testPrintWrappedIndentsContinuationLine()
{
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    java.io.StringWriter output = new java.io.StringWriter();

    formatter.printWrapped(new java.io.PrintWriter(output), 10, 4, "one two three");

    assertEquals("one two" + formatter.getNewLine()
            + "    three" + formatter.getNewLine(), output.toString());
}