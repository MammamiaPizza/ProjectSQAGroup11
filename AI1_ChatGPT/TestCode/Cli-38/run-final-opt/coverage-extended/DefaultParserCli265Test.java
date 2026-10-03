package org.apache.commons.cli.bug;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.junit.Test;

public class DefaultParserCli265Test {

    @Test
    public void concatenatedShortOptionsMustNotBeConsumedAsOptionalArgument() throws Exception {
        Option optional = new Option("x", true, "optional argument");
        optional.setOptionalArg(true);

        Options options = new Options();
        options.addOption(optional);
        options.addOption(new Option("a", false, "first flag"));
        options.addOption(new Option("b", false, "second flag"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-x", "-ab" });

        assertTrue(commandLine.hasOption("x"));
        assertNull(commandLine.getOptionValue("x"));
        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
    }

    @Test
    public void parsesConcatenatedShortFlags() throws Exception {
        Options options = new Options();
        options.addOption(new Option("a", false, "first flag"));
        options.addOption(new Option("b", false, "second flag"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-ab" });

        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
        assertNull(commandLine.getOptionValue("a"));
        assertNull(commandLine.getOptionValue("b"));
    }

    @Test
    public void parsesSeparateShortFlags() throws Exception {
        Options options = new Options();
        options.addOption(new Option("a", false, "first flag"));
        options.addOption(new Option("b", false, "second flag"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-a", "-b" });

        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
    }

    @Test
    public void concatenatedOptionsPassTrailingCharactersToArgumentOption() throws Exception {
        Options options = new Options();
        options.addOption(new Option("a", false, "flag"));
        options.addOption(new Option("b", true, "value option"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-abvalue" });

        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
        assertNull(commandLine.getOptionValue("a"));
        assertTrue("value".equals(commandLine.getOptionValue("b")));
    }

    @Test(expected = UnrecognizedOptionException.class)
    public void rejectsUnknownOptionWhenNotStoppingAtNonOption() throws Exception {
        Options options = new Options();
        options.addOption(new Option("a", false, "known flag"));

        new DefaultParser().parse(options, new String[] { "-z" });
    }

    @Test
    public void stopAtNonOptionPreservesUnknownTokenAndRemainingArguments() throws Exception {
        Options options = new Options();
        options.addOption(new Option("a", false, "known flag"));

        CommandLine commandLine = new DefaultParser().parse(options, new String[] { "-z", "-a" }, true);

        assertFalse(commandLine.hasOption("a"));
        assertArrayEquals(new String[] { "-z", "-a" }, commandLine.getArgs());
    }

@Test
public void parsesLongOptionWithAnEqualsValue() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("o", "output", true, ""));

    org.apache.commons.cli.CommandLine commandLine =
            new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--output=result" });

    org.junit.Assert.assertTrue(commandLine.hasOption("output"));
    org.junit.Assert.assertEquals("result", commandLine.getOptionValue("output"));
}

@Test(expected = org.apache.commons.cli.UnrecognizedOptionException.class)
public void rejectsEqualsValueForLongOptionWithoutArgument() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("v", "verbose", false, ""));

    new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--verbose=true" });
}

@Test(expected = org.apache.commons.cli.MissingOptionException.class)
public void rejectsMissingRequiredOption() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    org.apache.commons.cli.Option required = new org.apache.commons.cli.Option("r", "required", false, "");
    required.setRequired(true);
    options.addOption(required);

    new org.apache.commons.cli.DefaultParser().parse(options, new String[0]);
}

@Test(expected = org.apache.commons.cli.MissingArgumentException.class)
public void rejectsOptionMissingItsRequiredArgument() throws Exception {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options.addOption(new org.apache.commons.cli.Option("f", "file", true, ""));

    new org.apache.commons.cli.DefaultParser().parse(options, new String[] { "--file" });
}
}
