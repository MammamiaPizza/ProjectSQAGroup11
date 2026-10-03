public void testUsageUsesDefaultArgNameForLongOnlyOption()
{
    org.apache.commons.cli.Option option =
        new org.apache.commons.cli.Option(null, "file", true, "file to process");
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(option);

    java.io.StringWriter output = new java.io.StringWriter();
    new org.apache.commons.cli.HelpFormatter().printUsage(
        new java.io.PrintWriter(output),
        org.apache.commons.cli.HelpFormatter.DEFAULT_WIDTH,
        "app",
        options);

    assertEquals("usage: app --file <arg>" + System.getProperty("line.separator"),
                 output.toString());
}

public void testUsageFormatsOptionalOptionGroupInSortedOrder()
{
    org.apache.commons.cli.OptionGroup group = new org.apache.commons.cli.OptionGroup();
    group.addOption(new org.apache.commons.cli.Option("b", "second"));
    group.addOption(new org.apache.commons.cli.Option("a", "first"));

    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOptionGroup(group);

    java.io.StringWriter output = new java.io.StringWriter();
    new org.apache.commons.cli.HelpFormatter().printUsage(
        new java.io.PrintWriter(output),
        org.apache.commons.cli.HelpFormatter.DEFAULT_WIDTH,
        "app",
        options);

    assertEquals("usage: app [-a | -b]" + System.getProperty("line.separator"),
                 output.toString());
}

public void testUsageFormatsRequiredOptionGroupWithoutBrackets()
{
    org.apache.commons.cli.OptionGroup group = new org.apache.commons.cli.OptionGroup();
    group.setRequired(true);
    group.addOption(new org.apache.commons.cli.Option(null, "alpha", false, "alpha option"));

    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOptionGroup(group);

    java.io.StringWriter output = new java.io.StringWriter();
    new org.apache.commons.cli.HelpFormatter().printUsage(
        new java.io.PrintWriter(output),
        org.apache.commons.cli.HelpFormatter.DEFAULT_WIDTH,
        "app",
        options);

    assertEquals("usage: app --alpha" + System.getProperty("line.separator"),
                 output.toString());
}