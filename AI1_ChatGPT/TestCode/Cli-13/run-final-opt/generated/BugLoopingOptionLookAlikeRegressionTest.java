package org.apache.commons.cli2.bug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;
import org.apache.commons.cli2.option.ArgumentImpl;

public class BugLoopingOptionLookAlikeRegressionTest extends TestCase {

    private ArgumentImpl argument(final String name, final int minimum, final int maximum) {
        return new ArgumentImpl(name, null, minimum, maximum, '\0', '\0',
                null, null, null, 0);
    }

    private WriteableCommandLineImpl commandLine(final ArgumentImpl root) {
        return new WriteableCommandLineImpl(root, new ArrayList());
    }

    public void testUnexpectedValueUsesRawValueAndEmptyRootContext() throws Exception {
        final ArgumentImpl input = argument("", 0, 0);
        final WriteableCommandLineImpl commandLine = commandLine(input);
        commandLine.addValue(input, "testfile.txt");

        try {
            input.validate(commandLine, input);
            fail("An argument with maximum zero must reject a supplied value");
        } catch (OptionException expected) {
            assertEquals("Unexpected testfile.txt while processing ", expected.getMessage());
        }
    }

    public void testUnexpectedValueReportsFirstValueBeyondMaximum() throws Exception {
        final ArgumentImpl input = argument("", 0, 1);
        final WriteableCommandLineImpl commandLine = commandLine(input);
        commandLine.addValue(input, "first");
        commandLine.addValue(input, "second");

        try {
            input.validate(commandLine, input);
            fail("A second value must exceed an argument maximum of one");
        } catch (OptionException expected) {
            assertEquals("Unexpected second while processing ", expected.getMessage());
        }
    }

    public void testValuesWithinConfiguredBoundsValidate() throws Exception {
        final ArgumentImpl input = argument("input", 1, 2);
        final WriteableCommandLineImpl commandLine = commandLine(input);
        commandLine.addValue(input, "one");
        commandLine.addValue(input, "two");

        input.validate(commandLine, input);
        assertEquals(Arrays.asList(new String[] { "one", "two" }),
                commandLine.getValues(input));
    }

    public void testMissingRequiredValueIsRejected() throws Exception {
        final ArgumentImpl input = argument("input", 1, 1);
        final WriteableCommandLineImpl commandLine = commandLine(input);

        try {
            input.validate(commandLine, input);
            fail("A required argument must reject an empty value list");
        } catch (OptionException expected) {
            assertTrue(expected.getMessage().indexOf("input") >= 0);
        }
    }

    public void testAddingArgumentValueAlsoRecordsArgumentAsPresent() {
        final ArgumentImpl input = argument("input", 0, 1);
        final WriteableCommandLineImpl commandLine = commandLine(input);

        commandLine.addValue(input, "value");

        assertTrue(commandLine.hasOption(input));
        assertEquals(Collections.singletonList("value"), commandLine.getValues(input));
    }

    public void testExplicitValuesTakePrecedenceOverDefaults() {
        final ArgumentImpl input = argument("input", 0, 2);
        final WriteableCommandLineImpl commandLine = commandLine(input);
        final List defaults = Collections.singletonList("default");

        commandLine.setDefaultValues(input, defaults);
        commandLine.addValue(input, "specified");

        assertEquals(Collections.singletonList("specified"), commandLine.getValues(input));
    }

    public void testDefaultsAreReturnedWhenNoExplicitValuesExist() {
        final ArgumentImpl input = argument("input", 0, 2);
        final WriteableCommandLineImpl commandLine = commandLine(input);
        final List defaults = Collections.singletonList("default");

        commandLine.setDefaultValues(input, defaults);

        assertEquals(defaults, commandLine.getValues(input));
    }

    public void testBoundaryQuotesAreRemovedWithoutChangingUnquotedValue() {
        final ArgumentImpl input = argument("input", 0, 1);

        assertEquals("plain", input.stripBoundaryQuotes("plain"));
        assertEquals("quoted value", input.stripBoundaryQuotes("\"quoted value\""));
        assertEquals("quoted value", input.stripBoundaryQuotes("'quoted value'"));
    }

    public void testArgumentRootDoesNotClassifyDashPrefixedTextAsOption() {
        final ArgumentImpl input = argument("input", 0, 1);
        final WriteableCommandLineImpl commandLine = commandLine(input);

        assertFalse(commandLine.looksLikeOption("--not-configured"));
    }
}
