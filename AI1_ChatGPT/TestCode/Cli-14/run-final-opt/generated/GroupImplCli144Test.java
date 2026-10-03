package org.apache.commons.cli2.option;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.validation.FileValidator;

public class GroupImplCli144Test extends TestCase {

    public void testFileValidatedAnonymousArgumentIsStoredAsFile() throws Exception {
        final File file = File.createTempFile("cli144", ".tmp");
        try {
            final Argument argument = new ArgumentBuilder()
                    .withName("file")
                    .withValidator(FileValidator.getExistingFileInstance())
                    .create();
            final Group group = new GroupBuilder()
                    .withOption(argument)
                    .create();

            final Parser parser = new Parser();
            parser.setGroup(group);
            final CommandLine commandLine =
                    parser.parse(new String[] { file.getAbsolutePath() });

            final Object value = commandLine.getValue(argument);
            assertTrue(value instanceof File);
            assertEquals(file, value);
        } finally {
            file.delete();
        }
    }

    public void testAnonymousStringArgumentIsAcceptedAndStored() throws Exception {
        final Argument argument = new ArgumentBuilder()
                .withName("value")
                .create();
        final Group group = new GroupBuilder()
                .withOption(argument)
                .create();

        final Parser parser = new Parser();
        parser.setGroup(group);
        final CommandLine commandLine =
                parser.parse(new String[] { "plain-value" });

        assertEquals("plain-value", commandLine.getValue(argument));
    }

    public void testKnownOptionTriggerIsMatched() throws Exception {
        final Option option = new DefaultOptionBuilder()
                .withLongName("verbose")
                .create();
        final Group group = new GroupBuilder()
                .withOption(option)
                .create();

        final Parser parser = new Parser();
        parser.setGroup(group);
        final CommandLine commandLine =
                parser.parse(new String[] { "--verbose" });

        assertTrue(commandLine.hasOption(option));
    }

    public void testUnknownOptionIsNotConsumedAsAnonymousArgument() throws Exception {
        final Argument argument = new ArgumentBuilder()
                .withName("value")
                .create();
        final Group group = new GroupBuilder()
                .withOption(argument)
                .create();

        final Parser parser = new Parser();
        parser.setGroup(group);

        try {
            parser.parse(new String[] { "--unknown" });
            fail("An unrecognized option must not be accepted as an anonymous argument");
        } catch (OptionException expected) {
            assertNotNull(expected);
        }
    }
}
