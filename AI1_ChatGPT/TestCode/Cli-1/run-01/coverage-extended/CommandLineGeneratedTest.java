package org.apache.commons.cli;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class CommandLineGeneratedTest extends TestCase {

    private CommandLine parse(Options options, String[] arguments)
            throws ParseException {
        CommandLineParser parser = new PosixParser();
        return parser.parse(options, arguments);
    }

    // ---------------------------------------------------------------------
    // hasOption
    // ---------------------------------------------------------------------

    public void testHasOptionString() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine =
                parse(options, new String[] {"-a"});

        assertTrue(commandLine.hasOption("a"));
        assertFalse(commandLine.hasOption("b"));
    }

    public void testHasOptionChar() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine =
                parse(options, new String[] {"-a"});

        assertTrue(commandLine.hasOption('a'));
        assertFalse(commandLine.hasOption('b'));
    }

    // ---------------------------------------------------------------------
    // getOptionObject - coverage additions
    // ---------------------------------------------------------------------

    public void testGetOptionObjectMissingOptionReturnsNull()
            throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine =
                parse(options, new String[0]);

        assertFalse(commandLine.hasOption("a"));
        assertNull(commandLine.getOptionObject("a"));
    }

    public void testGetOptionObjectCharPresentWithoutArgumentReturnsNull()
            throws Exception {
        Options options = new Options();
        options.addOption("v", false, "verbose");

        CommandLine commandLine =
                parse(options, new String[] {"-v"});

        assertTrue(commandLine.hasOption('v'));
        assertNull(commandLine.getOptionValue('v'));
        assertNull(commandLine.getOptionObject('v'));
    }

    // ---------------------------------------------------------------------
    // getOptionValue - normal cases
    // ---------------------------------------------------------------------

    public void testGetOptionValueStringWithArgument() throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[] {"-f", "input.txt"});

        assertEquals("input.txt", commandLine.getOptionValue("f"));
    }

    public void testGetOptionValueCharWithArgument() throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[] {"-f", "input.txt"});

        assertEquals("input.txt", commandLine.getOptionValue('f'));
    }

    public void testGetOptionValueMissingOptionReturnsNull()
            throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine =
                parse(options, new String[0]);

        assertNull(commandLine.getOptionValue("a"));
        assertNull(commandLine.getOptionValue('a'));
    }

    // ---------------------------------------------------------------------
    // CLI-13 regression cases
    // ---------------------------------------------------------------------

    public void testGetOptionValuePresentWithoutArgumentReturnsNull()
            throws Exception {
        Options options = new Options();
        options.addOption("v", false, "verbose");

        CommandLine commandLine =
                parse(options, new String[] {"-v"});

        assertTrue(commandLine.hasOption("v"));
        assertNull(commandLine.getOptionValue("v"));
    }

    public void testGetOptionValueCharPresentWithoutArgumentReturnsNull()
            throws Exception {
        Options options = new Options();
        options.addOption("v", false, "verbose");

        CommandLine commandLine =
                parse(options, new String[] {"-v"});

        assertTrue(commandLine.hasOption('v'));
        assertNull(commandLine.getOptionValue('v'));
    }

    // ---------------------------------------------------------------------
    // getOptionValue with default
    // ---------------------------------------------------------------------

    public void testGetOptionValueStringDefaultWhenMissing()
            throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[0]);

        assertEquals("default.txt",
                commandLine.getOptionValue("f", "default.txt"));
    }

    public void testGetOptionValueStringWithArgumentIgnoresDefault()
            throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[] {"-f", "input.txt"});

        assertEquals("input.txt",
                commandLine.getOptionValue("f", "default.txt"));
    }

    public void testGetOptionValueCharDefaultWhenMissing()
            throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[0]);

        assertEquals("default.txt",
                commandLine.getOptionValue('f', "default.txt"));
    }

    public void testGetOptionValuePresentWithoutArgumentUsesDefault()
            throws Exception {
        Options options = new Options();
        options.addOption("v", false, "verbose");

        CommandLine commandLine =
                parse(options, new String[] {"-v"});

        assertEquals("default",
                commandLine.getOptionValue("v", "default"));
    }

    // ---------------------------------------------------------------------
    // getOptionValues
    // ---------------------------------------------------------------------

    public void testGetOptionValuesString() throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[] {"-f", "input.txt"});

        String[] values = commandLine.getOptionValues("f");

        assertNotNull(values);
        assertEquals(1, values.length);
        assertEquals("input.txt", values[0]);
    }

    public void testGetOptionValuesChar() throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[] {"-f", "input.txt"});

        String[] values = commandLine.getOptionValues('f');

        assertNotNull(values);
        assertEquals(1, values.length);
        assertEquals("input.txt", values[0]);
    }

    public void testGetOptionValuesMissingOptionReturnsNull()
            throws Exception {
        Options options = new Options();
        options.addOption("f", true, "file");

        CommandLine commandLine =
                parse(options, new String[0]);

        assertNull(commandLine.getOptionValues("f"));
        assertNull(commandLine.getOptionValues('f'));
    }

    public void testGetOptionValueUsingLongOptionName() throws Exception {
        Options options = new Options();
        options.addOption("f", "file", true, "file");

        CommandLine commandLine =
                parse(options, new String[] {"-f", "input.txt"});

        assertEquals("input.txt",
                commandLine.getOptionValue("file"));

        String[] values = commandLine.getOptionValues("file");

        assertNotNull(values);
        assertEquals(1, values.length);
        assertEquals("input.txt", values[0]);
    }

    // ---------------------------------------------------------------------
    // getArgs / getArgList
    // ---------------------------------------------------------------------

    public void testGetArgsWithRemainingArguments() throws Exception {
        Options options = new Options();

        CommandLine commandLine =
                parse(options, new String[] {"first", "second"});

        String[] arguments = commandLine.getArgs();

        assertNotNull(arguments);
        assertEquals(2, arguments.length);
        assertEquals("first", arguments[0]);
        assertEquals("second", arguments[1]);
    }

    public void testGetArgListWithRemainingArguments() throws Exception {
        Options options = new Options();

        CommandLine commandLine =
                parse(options, new String[] {"first", "second"});

        List arguments = commandLine.getArgList();

        assertNotNull(arguments);
        assertEquals(2, arguments.size());
        assertEquals("first", arguments.get(0));
        assertEquals("second", arguments.get(1));
    }

    public void testGetArgsWhenNoRemainingArguments() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");

        CommandLine commandLine =
                parse(options, new String[] {"-a"});

        assertNotNull(commandLine.getArgs());
        assertEquals(0, commandLine.getArgs().length);

        assertNotNull(commandLine.getArgList());
        assertEquals(0, commandLine.getArgList().size());
    }

    // ---------------------------------------------------------------------
    // addOption - long-only key branch
    // ---------------------------------------------------------------------

    public void testLongOnlyOptionIsIncludedInProcessedOptions() {
        CommandLine commandLine = new CommandLine();
        Option option =
                new Option(null, "verbose", false, "verbose option");

        commandLine.addOption(option);

        Option[] processed = commandLine.getOptions();

        assertNotNull(processed);
        assertEquals(1, processed.length);
        assertSame(option, processed[0]);
    }

    // ---------------------------------------------------------------------
    // iterator
    // ---------------------------------------------------------------------

    public void testIteratorReturnsProcessedOptions() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        CommandLine commandLine =
                parse(options, new String[] {"-a", "-b"});

        Iterator iterator = commandLine.iterator();

        int count = 0;
        boolean foundA = false;
        boolean foundB = false;

        while (iterator.hasNext()) {
            Option option = (Option) iterator.next();

            if ("a".equals(option.getOpt())) {
                foundA = true;
            }

            if ("b".equals(option.getOpt())) {
                foundB = true;
            }

            count++;
        }

        assertEquals(2, count);
        assertTrue(foundA);
        assertTrue(foundB);
    }

    public void testIteratorEmptyWhenNoOptionsProcessed()
            throws Exception {
        CommandLine commandLine =
                parse(new Options(), new String[0]);

        Iterator iterator = commandLine.iterator();

        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    // ---------------------------------------------------------------------
    // getOptions
    // ---------------------------------------------------------------------

    public void testGetOptionsReturnsProcessedOptions() throws Exception {
        Options options = new Options();
        options.addOption("a", false, "option a");
        options.addOption("b", false, "option b");

        CommandLine commandLine =
                parse(options, new String[] {"-a", "-b"});

        Option[] processed = commandLine.getOptions();

        assertNotNull(processed);
        assertEquals(2, processed.length);

        boolean foundA = false;
        boolean foundB = false;

        for (int i = 0; i < processed.length; i++) {
            if ("a".equals(processed[i].getOpt())) {
                foundA = true;
            }

            if ("b".equals(processed[i].getOpt())) {
                foundB = true;
            }
        }

        assertTrue(foundA);
        assertTrue(foundB);
    }

    public void testGetOptionsReturnsEmptyArrayWhenNoOptionsProcessed()
            throws Exception {
        CommandLine commandLine =
                parse(new Options(), new String[0]);

        Option[] processed = commandLine.getOptions();

        assertNotNull(processed);
        assertEquals(0, processed.length);
    }
}