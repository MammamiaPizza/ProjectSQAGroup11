package org.apache.commons.cli;

import java.util.Properties;

import junit.framework.TestCase;

public class PropertyOptionProcessingRegressionTest extends TestCase
{
    private CommandLineParser[] createParsers()
    {
        return new CommandLineParser[]
        {
            new BasicParser(),
            new DefaultParser(),
            new GnuParser(),
            new PosixParser()
        };
    }

    private CommandLine parse(CommandLineParser parser, Options options,
            String[] arguments, Properties properties) throws ParseException
    {
        if (parser instanceof DefaultParser)
        {
            return ((DefaultParser) parser).parse(options, arguments, properties);
        }
        return ((Parser) parser).parse(options, arguments, properties);
    }

    public void testPropertyOptionDoesNotOverrideSelectedOptionGroup() throws Exception
    {
        CommandLineParser[] parsers = createParsers();

        for (int i = 0; i < parsers.length; i++)
        {
            Options options = new Options();
            OptionGroup group = new OptionGroup();
            group.addOption(new Option("a", false, "first"));
            group.addOption(new Option("b", false, "second"));
            options.addOptionGroup(group);

            Properties properties = new Properties();
            properties.setProperty("b", "true");

            CommandLine commandLine = parse(
                    parsers[i],
                    options,
                    new String[] { "-a" },
                    properties);

            assertTrue(parsers[i].getClass().getName(), commandLine.hasOption("a"));
            assertFalse(parsers[i].getClass().getName(), commandLine.hasOption("b"));
        }
    }

    public void testUnexpectedPropertyIsIgnored() throws Exception
    {
        CommandLineParser[] parsers = createParsers();

        for (int i = 0; i < parsers.length; i++)
        {
            Options options = new Options();
            options.addOption(new Option("a", false, "known"));

            Properties properties = new Properties();
            properties.setProperty("unexpected", "true");
            properties.setProperty("a", "true");

            CommandLine commandLine = parse(parsers[i], options, null, properties);

            assertTrue(parsers[i].getClass().getName(), commandLine.hasOption("a"));
            assertFalse(parsers[i].getClass().getName(), commandLine.hasOption("unexpected"));
        }
    }

    public void testPropertySatisfiesRequiredOptionGroup() throws Exception
    {
        CommandLineParser[] parsers = createParsers();

        for (int i = 0; i < parsers.length; i++)
        {
            Options options = new Options();
            OptionGroup group = new OptionGroup();
            group.addOption(new Option("a", false, "first"));
            group.addOption(new Option("b", false, "second"));
            group.setRequired(true);
            options.addOptionGroup(group);

            Properties properties = new Properties();
            properties.setProperty("a", "true");

            CommandLine commandLine = parse(parsers[i], options, null, properties);

            assertTrue(parsers[i].getClass().getName(), commandLine.hasOption("a"));
            assertFalse(parsers[i].getClass().getName(), commandLine.hasOption("b"));
        }
    }

    public void testPropertySuppliesValueForRequiredArgumentOption() throws Exception
    {
        CommandLineParser[] parsers = createParsers();

        for (int i = 0; i < parsers.length; i++)
        {
            Options options = new Options();
            Option option = new Option("p", true, "property value");
            option.setRequired(true);
            options.addOption(option);

            Properties properties = new Properties();
            properties.setProperty("p", "configured-value");

            CommandLine commandLine = parse(parsers[i], options, null, properties);

            assertTrue(parsers[i].getClass().getName(), commandLine.hasOption("p"));
            assertEquals("configured-value", commandLine.getOptionValue("p"));
        }
    }
}
