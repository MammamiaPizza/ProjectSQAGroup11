package org.apache.commons.cli;

import java.util.Properties;

import junit.framework.TestCase;

public class ParserPropertyOptionsTest extends TestCase
{
    public void testPropertyBooleanFlagsAreAppliedOnlyForTrueValues() throws Exception
    {
        Options options = new Options();
        options.addOption("enabled", false, "enabled flag");
        options.addOption("disabled", false, "disabled flag");

        Properties properties = new Properties();
        properties.setProperty("enabled", "true");
        properties.setProperty("disabled", "false");

        CommandLine commandLine = new PosixParser().parse(options, new String[0], properties);

        assertTrue(commandLine.hasOption("enabled"));
        assertFalse(commandLine.hasOption("disabled"));
    }

    public void testPropertyBooleanFlagRecognizesYesAndOneButNotNoOrZero() throws Exception
    {
        Options options = new Options();
        options.addOption("yesFlag", false, "yes flag");
        options.addOption("oneFlag", false, "one flag");
        options.addOption("noFlag", false, "no flag");
        options.addOption("zeroFlag", false, "zero flag");

        Properties properties = new Properties();
        properties.setProperty("yesFlag", "yes");
        properties.setProperty("oneFlag", "1");
        properties.setProperty("noFlag", "no");
        properties.setProperty("zeroFlag", "0");

        CommandLine commandLine = new PosixParser().parse(options, new String[0], properties);

        assertTrue(commandLine.hasOption("yesFlag"));
        assertTrue(commandLine.hasOption("oneFlag"));
        assertFalse(commandLine.hasOption("noFlag"));
        assertFalse(commandLine.hasOption("zeroFlag"));
    }

    public void testPropertySuppliesValueForArgumentOptionWhenCommandLineDoesNot() throws Exception
    {
        Options options = new Options();
        options.addOption("config", true, "configuration value");

        Properties properties = new Properties();
        properties.setProperty("config", "fromProperties");

        CommandLine commandLine = new PosixParser().parse(options, new String[0], properties);

        assertTrue(commandLine.hasOption("config"));
        assertEquals("fromProperties", commandLine.getOptionValue("config"));
    }

    public void testCommandLineOptionTakesPrecedenceOverPropertyValue() throws Exception
    {
        Options options = new Options();
        options.addOption("config", true, "configuration value");

        Properties properties = new Properties();
        properties.setProperty("config", "fromProperties");

        CommandLine commandLine = new PosixParser().parse(
                options,
                new String[] { "-config", "fromCommandLine" },
                properties);

        assertTrue(commandLine.hasOption("config"));
        assertEquals("fromCommandLine", commandLine.getOptionValue("config"));
    }

    public void testUnknownCommandLineOptionIsRejected() throws Exception
    {
        Options options = new Options();

        try
        {
            new PosixParser().parse(options, new String[] { "-unknown" });
            fail("An unrecognized option must cause a parse failure");
        }
        catch (UnrecognizedOptionException expected)
        {
        }
    }
}
