package org.apache.commons.cli;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class CommandLineCLITest extends TestCase {

    private CommandLine parse(Options options, String[] arguments) throws ParseException {
        return new GnuParser().parse(options, arguments);
    }

    public void testLongOptionIsRecognizedByShortAndLongNames() throws Exception {
        Options options = new Options();
        options.addOption(new Option("f", "file", true, "input file"));

        CommandLine line = parse(options, new String[] { "--file", "report.txt" });

        assertTrue(line.hasOption("f"));
        assertTrue(line.hasOption('f'));
        assertTrue(line.hasOption("file"));
        assertEquals("report.txt", line.getOptionValue("f"));
        assertEquals("report.txt", line.getOptionValue("file"));
        assertEquals("report.txt", line.getOptionValue("--file"));
    }

    public void testGetOptionObjectSupportsLongOptionName() throws Exception {
        Options options = new Options();
        Option number = new Option("n", "number", true, "numeric value");
        number.setType(Number.class);
        options.addOption(number);

        CommandLine line = parse(options, new String[] { "--number", "42" });

        Object value = line.getOptionObject("number");
        assertNotNull(value);
        assertTrue(value instanceof Number);
        assertEquals(42, ((Number) value).intValue());
        assertEquals(((Number) value).intValue(),
                     ((Number) line.getOptionObject('n')).intValue());
    }

    public void testAbsentOptionReturnsNullAndDefaultValue() throws Exception {
        Options options = new Options();
        options.addOption(new Option("f", "file", true, "input file"));

        CommandLine line = parse(options, new String[0]);

        assertFalse(line.hasOption("f"));
        assertFalse(line.hasOption("file"));
        assertNull(line.getOptionValue("file"));
        assertNull(line.getOptionValues("file"));
        assertNull(line.getOptionObject("file"));
        assertEquals("default.txt", line.getOptionValue("file", "default.txt"));
        assertEquals("fallback", line.getOptionValue('f', "fallback"));
    }

    public void testMultipleOptionValuesAreReturnedInOrder() throws Exception {
        Options options = new Options();
        Option include = new Option("I", "include", true, "include paths");
        include.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(include);

        CommandLine line = parse(options,
                                 new String[] { "--include", "one", "two", "three" });

        String[] values = line.getOptionValues("include");
        assertNotNull(values);
        assertEquals(3, values.length);
        assertEquals("one", values[0]);
        assertEquals("two", values[1]);
        assertEquals("three", values[2]);
        assertEquals("one", line.getOptionValue('I'));
    }

    public void testRemainingArgumentsPreserveOrderAndAreExposedAsList() throws Exception {
        Options options = new Options();
        options.addOption(new Option("q", "quiet", false, "quiet mode"));

        CommandLine line = parse(options, new String[] { "-q", "first", "second" });

        String[] args = line.getArgs();
        assertEquals(2, args.length);
        assertEquals("first", args[0]);
        assertEquals("second", args[1]);

        List argList = line.getArgList();
        assertEquals(2, argList.size());
        assertEquals("first", argList.get(0));
        assertEquals("second", argList.get(1));
    }

    public void testEmptyCommandLineHasNoArgumentsOrOptions() throws Exception {
        CommandLine line = parse(new Options(), new String[0]);

        assertEquals(0, line.getArgs().length);
        assertEquals(0, line.getArgList().size());
        assertEquals(0, line.getOptions().length);
        assertFalse(line.iterator().hasNext());
    }

    public void testIteratorAndGetOptionsExposeSameProcessedOptions() throws Exception {
        Options options = new Options();
        Option alpha = new Option("a", "alpha", false, "alpha option");
        Option beta = new Option("b", "beta", false, "beta option");
        options.addOption(alpha);
        options.addOption(beta);

        CommandLine line = parse(options, new String[] { "--alpha", "-b" });

        Option[] optionArray = line.getOptions();
        assertEquals(2, optionArray.length);

        Set fromArray = new HashSet();
        for (int i = 0; i < optionArray.length; i++) {
            fromArray.add(optionArray[i]);
        }

        Set fromIterator = new HashSet();
        Iterator iterator = line.iterator();
        while (iterator.hasNext()) {
            fromIterator.add(iterator.next());
        }

        assertEquals(fromArray, fromIterator);
        assertTrue(fromArray.contains(alpha));
        assertTrue(fromArray.contains(beta));
    }
}