import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DefaultParserCLI265Test {

    @Test
    public void shouldParseLongOptionAfterShortOptionWithOptionalArgumentAndNoValue() throws Exception {
        Option first = new Option("a", true, "optional argument");
        first.setOptionalArg(true);

        Options options = new Options();
        options.addOption(first);
        options.addOption(new Option("l", "last", false, "last option"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-a", "-last" });

        assertTrue(commandLine.hasOption("a"));
        assertNull(commandLine.getOptionValue("a"));
        assertTrue(commandLine.hasOption("last"));
    }

    @Test
    public void shouldParseSecondLongOptionAfterShortFlagWithoutArgument() throws Exception {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag"));
        options.addOption(new Option("l", "last", false, "last option"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-a", "-last" });

        assertTrue(commandLine.hasOption("a"));
        assertNull(commandLine.getOptionValue("a"));
        assertTrue(commandLine.hasOption("last"));
    }

    @Test
    public void shouldConsumeNonOptionAsRequiredShortOptionArgument() throws Exception {
        Options options = new Options();
        options.addOption(new Option("f", true, "file"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-f", "input.txt" });

        assertTrue(commandLine.hasOption("f"));
        assertEquals("input.txt", commandLine.getOptionValue("f"));
        assertEquals(0, commandLine.getArgs().length);
    }

    @Test
    public void shouldRejectRequiredArgumentWhenNextTokenIsRecognizedOption() throws Exception {
        Options options = new Options();
        options.addOption(new Option("f", true, "file"));
        options.addOption(new Option("v", false, "verbose"));

        try {
            new DefaultParser().parse(options, new String[] { "-f", "-v" });
            fail("A required option argument must not be replaced by another option");
        } catch (MissingArgumentException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void shouldRejectRequiredArgumentAtEndOfInput() throws Exception {
        Options options = new Options();
        options.addOption(new Option("f", true, "file"));

        try {
            new DefaultParser().parse(options, new String[] { "-f" });
            fail("A required option argument is missing");
        } catch (MissingArgumentException expected) {
            assertNotNull(expected);
        }
    }

    @Test
    public void shouldTreatTokensAfterDoubleDashAsArguments() throws Exception {
        Options options = new Options();
        options.addOption(new Option("l", "last", false, "last option"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "--", "--last" });

        assertFalse(commandLine.hasOption("last"));
        assertEquals(1, commandLine.getArgs().length);
        assertEquals("--last", commandLine.getArgs()[0]);
    }

    @Test
    public void shouldRejectUnknownOptionWhenNotStoppingAtNonOption() throws Exception {
        Options options = new Options();
        options.addOption(new Option("a", false, "known option"));

        try {
            new DefaultParser().parse(options, new String[] { "-unknown" });
            fail("Unknown options must be rejected by default");
        } catch (UnrecognizedOptionException expected) {
            assertNotNull(expected);
        }
    }
}
