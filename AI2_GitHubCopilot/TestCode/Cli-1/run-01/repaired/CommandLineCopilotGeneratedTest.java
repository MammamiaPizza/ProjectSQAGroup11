package org.apache.commons.cli;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class CommandLineCopilotGeneratedTest extends TestCase {

    private PosixParser parser;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new PosixParser();
    }

    public void testHasOptionReportsPresenceForShortNames() throws Exception {
        Options options = new Options();
        options.addOption("f", "file", true, "file name");
        options.addOption("v", false, "verbose");

        CommandLine cmd = parser.parse(options, new String[] { "-f", "report.txt", "-v" });

        assertTrue(cmd.hasOption("f"));
        assertTrue(cmd.hasOption('f'));
        assertTrue(cmd.hasOption("v"));
        assertFalse(cmd.hasOption("missing"));
    }

    public void testGetOptionValueReturnsArgumentAccordingToJavadoc() throws Exception {
        Options options = new Options();
        options.addOption("f", "file", true, "file name");

        CommandLine cmd = parser.parse(options, new String[] { "-f", "report.txt" });

        assertEquals("report.txt", cmd.getOptionValue("f"));
        assertEquals("report.txt", cmd.getOptionValue('f'));
        assertEquals("report.txt", cmd.getOptionValue("file"));
    }

    public void testGetOptionValueReturnsNullForMissingOrFlagOption() throws Exception {
        Options options = new Options();
        options.addOption("f", "file", true, "file name");
        options.addOption("v", false, "verbose");

        CommandLine cmd = parser.parse(options, new String[] { "-v" });

        assertNull(cmd.getOptionValue("f"));
        assertNull(cmd.getOptionValue("file"));
        assertNull(cmd.getOptionValue("missing"));
        assertNull(cmd.getOptionValue("v"));
    }

    public void testGetOptionValueDefaultReturnsDefaultWhenNoValue() throws Exception {
        Options options = new Options();
        options.addOption("f", "file", true, "file name");
        options.addOption("v", false, "verbose");

        CommandLine cmd = parser.parse(options, new String[] { "-v" });

        assertEquals("fallback", cmd.getOptionValue("f", "fallback"));
        assertEquals("fallback", cmd.getOptionValue("missing", "fallback"));
        assertEquals("fallback", cmd.getOptionValue('f', "fallback"));
        assertEquals("default", cmd.getOptionValue("v", "default"));
    }

    public void testGetOptionValuesReturnsAllValuesAndNullWhenMissing() throws Exception {
        Options options = new Options();
        Option multi = new Option("f", "file", true, "file names");
        multi.setArgs(2);
        options.addOption(multi);

        CommandLine cmd = parser.parse(options, new String[] { "-f", "one", "two" });

        String[] values = cmd.getOptionValues("f");
        assertNotNull(values);
        assertEquals(2, values.length);
        assertEquals("one", values[0]);
        assertEquals("two", values[1]);
        assertEquals("one", cmd.getOptionValue("f"));
        assertNull(cmd.getOptionValues("missing"));
    }

    public void testGetOptionObjectConvertsLongAndShortOptionValues() throws Exception {
        Options options = new Options();
        Option integer = new Option("n", "number", true, "numeric value");
        integer.setType(Integer.class);
        options.addOption(integer);

        CommandLine cmd = parser.parse(options, new String[] { "--number", "42" });

        assertTrue(cmd.hasOption("n"));
        assertEquals(Integer.valueOf(42), cmd.getOptionObject("n"));
        assertEquals(Integer.valueOf(42), cmd.getOptionObject('n'));
        assertEquals(Integer.valueOf(42), cmd.getOptionObject("number"));
        assertNull(cmd.getOptionObject("missing"));
    }

    public void testArgsAndArgListExposeUnparsedTokens() throws Exception {
        Options options = new Options();
        options.addOption("f", "file", true, "file name");

        CommandLine cmd = parser.parse(options, new String[] { "-f", "report.txt", "tail1", "tail2" });

        assertEquals(2, cmd.getArgs().length);
        assertEquals("tail1", cmd.getArgs()[0]);
        assertEquals("tail2", cmd.getArgs()[1]);

        List args = cmd.getArgList();
        assertEquals(2, args.size());
        assertEquals("tail1", args.get(0));
        assertEquals("tail2", args.get(1));
    }

    public void testIteratorAndGetOptionsContainProcessedOptions() throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file name");
        options.addOption("v", false, "verbose");

        CommandLine cmd = parser.parse(options, new String[] { "-f", "report.txt", "-v" });

        assertEquals(2, cmd.getOptions().length);

        Iterator iterator = cmd.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(2, count);
        assertTrue(cmd.hasOption("f"));
        assertTrue(cmd.hasOption("v"));
    }
}