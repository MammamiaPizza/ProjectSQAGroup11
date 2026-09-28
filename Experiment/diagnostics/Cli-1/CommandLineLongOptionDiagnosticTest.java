package org.apache.commons.cli;

import junit.framework.TestCase;

public class CommandLineLongOptionDiagnosticTest extends TestCase {
    public void testGetOptionObjectByLongName() throws Exception {
        Options options = new Options();
        Option number = new Option("n", "number", true, "numeric value");
        number.setType(PatternOptionBuilder.NUMBER_VALUE);
        options.addOption(number);

        CommandLine cmd = new PosixParser().parse(
            options, new String[] {"--number", "42"});

        assertEquals(42, ((Number) cmd.getOptionObject("number")).intValue());
    }
}
