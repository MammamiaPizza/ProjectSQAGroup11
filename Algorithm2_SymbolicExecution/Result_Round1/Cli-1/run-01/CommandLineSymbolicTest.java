package org.apache.commons.cli;

import java.util.Iterator;
import junit.framework.TestCase;

public class CommandLineSymbolicTest extends TestCase {

    private CommandLine parse(Options options, String[] args)
            throws ParseException {
        return new PosixParser().parse(options, args);
    }

    /*
     * Symbolic path:
     * optionPresent = false
     *
     * getOptionValues:
     *   names.containsKey(opt) = false
     *   options.containsKey(key) = false
     *   -> null
     *
     * getOptionValue -> null
     */
    public void testPath01AbsentOptionReturnsNull() throws Exception {
        Options options = new Options();
        options.addOption("a", true, "argument");

        CommandLine cmd = parse(options, new String[0]);

        assertFalse(cmd.hasOption("a"));
        assertNull(cmd.getOptionValues("a"));
        assertNull(cmd.getOptionValue("a"));
    }

    /*
     * Symbolic path:
     * optionPresent = true
     * hasValue = true
     * short-name lookup
     */
    public void testPath02ShortOptionWithValue() throws Exception {
        Options options = new Options();
        options.addOption("n", true, "number");

        CommandLine cmd =
                parse(options, new String[] {"-n", "42"});

        assertTrue(cmd.hasOption("n"));
        assertEquals("42", cmd.getOptionValue("n"));

        String[] values = cmd.getOptionValues("n");
        assertNotNull(values);
        assertEquals(1, values.length);
        assertEquals("42", values[0]);
    }

    /*
     * Symbolic path:
     * optionPresent = true
     * hasValue = false
     *
     * Contract: an option without an argument has no option value.
     */
    public void testPath03PresentFlagWithoutValue() throws Exception {
        Options options = new Options();
        options.addOption("v", false, "verbose");

        CommandLine cmd =
                parse(options, new String[] {"-v"});

        assertTrue(cmd.hasOption("v"));
        assertNull(cmd.getOptionValue("v"));
    }

    /*
     * Symbolic path:
     * answer == null in getOptionValue(opt, defaultValue)
     */
    public void testPath04MissingOptionUsesDefault() throws Exception {
        Options options = new Options();
        options.addOption("n", true, "number");

        CommandLine cmd = parse(options, new String[0]);

        assertEquals(
                "default",
                cmd.getOptionValue("n", "default"));
    }

    /*
     * Symbolic path:
     * answer != null in getOptionValue(opt, defaultValue)
     */
    public void testPath05PresentValueOverridesDefault()
            throws Exception {
        Options options = new Options();
        options.addOption("n", true, "number");

        CommandLine cmd =
                parse(options, new String[] {"-n", "42"});

        assertEquals(
                "42",
                cmd.getOptionValue("n", "default"));
    }

    /*
     * Symbolic alias path:
     *
     * queried opt = "number"
     * names.containsKey("number") = true
     * key = "n"
     * options.containsKey("n") = true
     *
     * This exercises long-name -> short-key canonicalization.
     */
    public void testPath06LongAliasResolvesValue() throws Exception {
        Options options = new Options();
        options.addOption(
                "n", "number", true, "number");

        CommandLine cmd =
                parse(options,
                        new String[] {"--number", "42"});

        assertEquals("42", cmd.getOptionValue("number"));

        String[] values = cmd.getOptionValues("number");
        assertNotNull(values);
        assertEquals(1, values.length);
        assertEquals("42", values[0]);
    }

    /*
     * Same alias path with leading hyphens.
     * getOptionValues strips leading hyphens before lookup.
     */
    public void testPath07HyphenatedLongAlias() throws Exception {
        Options options = new Options();
        options.addOption(
                "n", "number", true, "number");

        CommandLine cmd =
                parse(options,
                        new String[] {"--number", "42"});

        assertEquals(
                "42",
                cmd.getOptionValue("--number"));
    }

    /*
     * char-overload delegation paths.
     */
    public void testPath08CharDelegation() throws Exception {
        Options options = new Options();
        options.addOption("n", true, "number");

        CommandLine cmd =
                parse(options, new String[] {"-n", "42"});

        assertTrue(cmd.hasOption('n'));
        assertEquals("42", cmd.getOptionValue('n'));

        String[] values = cmd.getOptionValues('n');
        assertNotNull(values);
        assertEquals("42", values[0]);
    }

    /*
     * getOptionObject short-name path:
     *
     * getOptionValue("n") != null
     * options.containsKey("n") = true
     * res != null
     * -> TypeHandler.createValue(...)
     */
    public void testPath09TypedObjectByShortName()
            throws Exception {
        Options options = new Options();

        Option number =
                new Option(
                        "n",
                        "number",
                        true,
                        "numeric value");

        number.setType(
                PatternOptionBuilder.NUMBER_VALUE);

        options.addOption(number);

        CommandLine cmd =
                parse(options,
                        new String[] {"-n", "42"});

        Object object = cmd.getOptionObject("n");

        assertNotNull(object);
        assertTrue(object instanceof Number);
        assertEquals(
                42,
                ((Number) object).intValue());
    }

    /*
     * Alias/object symbolic path.
     *
     * getOptionValue("number"):
     *   names.containsKey("number") = true
     *   canonical key = "n"
     *   value = "42"
     *
     * The option represented by long name "number" is the same
     * processed typed option. The public CommandLine query should
     * therefore return its converted object for that option name.
     *
     * This path is intentionally kept because the source contains
     * different lookup conditions between getOptionValue(String)
     * and getOptionObject(String).
     */
    public void testPath10TypedObjectByLongName()
            throws Exception {
        Options options = new Options();

        Option number =
                new Option(
                        "n",
                        "number",
                        true,
                        "numeric value");

        number.setType(
                PatternOptionBuilder.NUMBER_VALUE);

        options.addOption(number);

        CommandLine cmd =
                parse(options,
                        new String[] {"--number", "42"});

        assertEquals(
                "42",
                cmd.getOptionValue("number"));

        Object object =
                cmd.getOptionObject("number");

        assertNotNull(object);
        assertTrue(object instanceof Number);
        assertEquals(
                42,
                ((Number) object).intValue());
    }

    /*
     * getOptionObject absent-option path.
     */
    public void testPath11MissingObjectReturnsNull()
            throws Exception {
        Options options = new Options();
        options.addOption("n", true, "number");

        CommandLine cmd =
                parse(options, new String[0]);

        assertNull(cmd.getOptionObject("n"));
    }

    /*
     * processed-option collection paths.
     */
    public void testPath12ProcessedOptions()
            throws Exception {
        Options options = new Options();
        options.addOption("a", false, "a");
        options.addOption("b", false, "b");

        CommandLine cmd =
                parse(options,
                        new String[] {"-a", "-b"});

        Option[] processed = cmd.getOptions();

        assertEquals(2, processed.length);

        Iterator iterator = cmd.iterator();
        int count = 0;

        while (iterator.hasNext()) {
            assertNotNull(iterator.next());
            count++;
        }

        assertEquals(2, count);
    }

    /*
     * leftover argument collection path.
     */
    public void testPath13RemainingArguments()
            throws Exception {
        CommandLine cmd =
                parse(
                        new Options(),
                        new String[] {
                            "first",
                            "second"
                        });

        assertEquals(2, cmd.getArgs().length);
        assertEquals("first", cmd.getArgs()[0]);
        assertEquals("second", cmd.getArgs()[1]);

        assertEquals(2, cmd.getArgList().size());
    }

    /*
     * addOption key == null symbolic branch.
     *
     * Tests are in org.apache.commons.cli so the package-private
     * CommandLine constructor/addOption methods are reachable.
     */
    public void testPath14LongOnlyOptionKey()
            throws Exception {
        CommandLine cmd =
                new CommandLine();

        Option option =
                new Option(
                        null,
                        "verbose",
                        false,
                        "verbose");

        cmd.addOption(option);

        assertTrue(cmd.hasOption("verbose"));

        Option[] processed = cmd.getOptions();
        assertEquals(1, processed.length);
        assertSame(option, processed[0]);
    }
}
