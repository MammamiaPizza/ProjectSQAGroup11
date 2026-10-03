package org.apache.commons.cli2.builder;

import junit.framework.TestCase;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.commandline.Parser;

public class PatternBuilderTest extends TestCase {

    public void testRequiredOptionIsReportedInsteadOfCompetingArgumentOption()
        throws Exception {
        final Group group = buildGroup("!hc:");
        final Parser parser = new Parser();
        parser.setGroup(group);

        try {
            parser.parse(new String[] { "-c", "value" });
            fail("A missing required -h option should cause parsing to fail");
        }
        catch (final OptionException expected) {
            assertEquals("[-h]", expected.getMessage());
        }
    }

    public void testRequiredOptionAndArgumentOptionCanBothBeParsed()
        throws Exception {
        final Group group = buildGroup("!hc:");
        final Parser parser = new Parser();
        parser.setGroup(group);

        assertNotNull(parser.parse(new String[] { "-h", "-c", "value" }));
    }

    public void testOptionalArgumentOptionDoesNotRequireOtherOptionalOption()
        throws Exception {
        final Group group = buildGroup("hc:");
        final Parser parser = new Parser();
        parser.setGroup(group);

        assertNotNull(parser.parse(new String[] { "-c", "value" }));
    }

    public void testRequiredMarkerBuildsRequiredSingleOption() {
        final PatternBuilder builder = new PatternBuilder();

        builder.withPattern("!h");
        final Option option = builder.create();

        assertEquals("-h", option.getPreferredName());
        assertTrue(option.isRequired());
    }

    public void testCreateClearsPreviouslyBuiltOptions() throws Exception {
        final PatternBuilder builder = new PatternBuilder();
        builder.withPattern("!hc:");
        builder.create();

        builder.withPattern("cd:");
        final Group secondGroup = (Group) builder.create();

        final Parser parser = new Parser();
        parser.setGroup(secondGroup);
        assertNotNull(parser.parse(new String[] { "-c", "value" }));
    }

    public void testResetClearsPreviouslyBuiltRequiredOptions() throws Exception {
        final PatternBuilder builder = new PatternBuilder();
        builder.withPattern("!hc:");
        builder.reset();
        builder.withPattern("cd:");

        final Group group = (Group) builder.create();
        final Parser parser = new Parser();
        parser.setGroup(group);

        assertNotNull(parser.parse(new String[] { "-c", "value" }));
    }

    public void testSeparatePatternsAccumulateIntoOneGroup() throws Exception {
        final PatternBuilder builder = new PatternBuilder();
        builder.withPattern("!h");
        builder.withPattern("c:");

        final Group group = (Group) builder.create();
        final Parser parser = new Parser();
        parser.setGroup(group);

        try {
            parser.parse(new String[] { "-c", "value" });
            fail("The required option from the first pattern must remain required");
        }
        catch (final OptionException expected) {
            assertEquals("[-h]", expected.getMessage());
        }
    }

    private Group buildGroup(final String pattern) {
        final PatternBuilder builder = new PatternBuilder();
        builder.withPattern(pattern);
        return (Group) builder.create();
    }
}
