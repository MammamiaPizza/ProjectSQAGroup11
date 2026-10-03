public void testFindWrapPosRecognizesNewlineAndTabAsBreaks()
{
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();

    assertEquals(4, formatter.findWrapPos("abc\ndef", 6, 0));
    assertEquals(4, formatter.findWrapPos("abc\tdef", 6, 0));
}

public void testPrintUsageFormatsOptionalOptionGroupInSortedOrder()
{
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.OptionGroup group = new org.apache.commons.cli.OptionGroup();

    group.addOption(new org.apache.commons.cli.Option("b", false, ""));
    group.addOption(new org.apache.commons.cli.Option("a", false, ""));
    options.addOptionGroup(group);

    java.io.StringWriter output = new java.io.StringWriter();
    formatter.printUsage(new java.io.PrintWriter(output), 80, "app", options);

    assertEquals("usage: app [-a | -b]", output.toString().trim());
}

public void testPrintUsageUsesLongOptionSeparatorForRequiredLongOnlyArgument()
{
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.Option option =
        new org.apache.commons.cli.Option(null, "file", true, "");

    option.setRequired(true);
    options.addOption(option);
    formatter.setLongOptSeparator("=");

    java.io.StringWriter output = new java.io.StringWriter();
    formatter.printUsage(new java.io.PrintWriter(output), 80, "app", options);

    assertEquals("usage: app --file=<arg>", output.toString().trim());
}