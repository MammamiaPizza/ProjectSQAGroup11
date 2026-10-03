package org.apache.commons.cli2.bug;

import junit.framework.TestCase;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.validation.NumberValidator;

public class BugCLI150NegativeNumberTest extends TestCase {

    private CommandLine parse(final String[] arguments) throws OptionException {
        final Argument numberArgument = new ArgumentBuilder()
                .withName("num")
                .withMinimum(1)
                .withMaximum(1)
                .withValidator(NumberValidator.getIntegerInstance())
                .create();

        final Option numberOption = new DefaultOption.Builder()
                .withLongName("num")
                .withArgument(numberArgument)
                .create();

        final Group group = new GroupBuilder()
                .withOption(numberOption)
                .create();

        final Parser parser = new Parser();
        parser.setGroup(group);
        return parser.parse(arguments);
    }

    public void testNegativeIntegerIsAcceptedAsOptionValue() throws Exception {
        final CommandLine commandLine = parse(new String[] { "--num", "-42" });

        assertEquals(new Integer(-42), commandLine.getValue("num"));
    }

    public void testNegativeZeroIsAcceptedAsOptionValue() throws Exception {
        final CommandLine commandLine = parse(new String[] { "--num", "-0" });

        assertEquals(new Integer(0), commandLine.getValue("num"));
    }

    public void testRecognizedOptionTriggerIsNotConsumedAsIntegerValue() throws Exception {
        try {
            parse(new String[] { "--num", "--num" });
            fail("A required integer value must not be replaced by another option trigger");
        } catch (OptionException expected) {
            assertNotNull(expected.getMessage());
        }
    }
}