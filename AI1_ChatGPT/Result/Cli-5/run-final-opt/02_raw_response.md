package org.apache.commons.cli;

import junit.framework.TestCase;

public class UtilCli133Test extends TestCase {

    public void testStripLeadingHyphensReturnsNullForNullInput() {
        assertNull(Util.stripLeadingHyphens(null));
    }

    public void testStripLeadingHyphensRemovesOnlyOneLeadingPrefix() {
        assertEquals("option", Util.stripLeadingHyphens("--option"));
        assertEquals("option", Util.stripLeadingHyphens("-option"));
        assertEquals("option", Util.stripLeadingHyphens("option"));
    }

    public void testStripLeadingHyphensSupportsHyphenOnlyAndEmptyInputs() {
        assertEquals("", Util.stripLeadingHyphens("--"));
        assertEquals("", Util.stripLeadingHyphens("-"));
        assertEquals("", Util.stripLeadingHyphens(""));
    }

    public void testStripLeadingAndTrailingQuotes() {
        assertEquals("one two", Util.stripLeadingAndTrailingQuotes("\"one two\""));
        assertEquals("plain", Util.stripLeadingAndTrailingQuotes("plain"));
        assertEquals("", Util.stripLeadingAndTrailingQuotes("\"\""));
        assertEquals("", Util.stripLeadingAndTrailingQuotes(""));
    }

    public void testLongOnlyOptionsAreParsedInCommandLineOrder() throws Exception {
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("first").create());
        options.addOption(OptionBuilder.withLongOpt("second").create());

        CommandLine commandLine = new GnuParser().parse(
                options, new String[] { "--second", "--first" });

        Option[] parsedOptions = commandLine.getOptions();
        assertEquals(2, parsedOptions.length);
        assertEquals("second", parsedOptions[0].getLongOpt());
        assertEquals("first", parsedOptions[1].getLongOpt());
    }
}