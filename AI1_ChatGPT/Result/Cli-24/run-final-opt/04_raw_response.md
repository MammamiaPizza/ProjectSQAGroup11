public void testPrintHelpHandlesOptionColumnWiderThanConfiguredWidth()
{
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("a", "very-long-option-name", false, "text"));

    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
    java.io.PrintWriter writer = new java.io.PrintWriter(output);

    formatter.printHelp(writer, 10, "cmd", null, options, 1, 3, null);
    writer.flush();

    assertTrue(output.toString().indexOf("text") >= 0);
}