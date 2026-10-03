package org.apache.commons.cli2.commandline;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;

public class WriteableCommandLineImplTest extends TestCase {

    public void testAddValueRetainsAllValuesInInsertionOrder() {
        final Option option = createOption(3);
        final WriteableCommandLineImpl commandLine =
                new WriteableCommandLineImpl(option, Collections.EMPTY_LIST);

        commandLine.addValue(option, "1");
        commandLine.addValue(option, "2");
        commandLine.addValue(option, "10000");

        assertEquals(Arrays.asList(new String[] { "1", "2", "10000" }),
                commandLine.getValues(option, null));
    }

    public void testRepeatedSingleArgumentOptionRetainsValues() throws Exception {
        final Option option = createOption(1);
        final CommandLine commandLine = parse(option,
                Arrays.asList(new String[] { "-v", "1", "-v", "1000" }));

        assertEquals(Arrays.asList(new String[] { "1", "1000" }),
                commandLine.getValues(option));
    }

    public void testMaximumNumberOfArgumentsRetainsFinalValue() throws Exception {
        final Option option = createOption(3);
        final CommandLine commandLine = parse(option,
                Arrays.asList(new String[] { "-v", "1", "2", "10000" }));

        assertEquals(Arrays.asList(new String[] { "1", "2", "10000" }),
                commandLine.getValues(option));
    }

    public void testSuppliedDefaultValuesAreUsedWhenNoValuesWereAdded() {
        final Option option = createOption(1);
        final WriteableCommandLineImpl commandLine =
                new WriteableCommandLineImpl(option, Collections.EMPTY_LIST);
        final List defaults = Arrays.asList(new String[] { "default" });

        assertEquals(defaults, commandLine.getValues(option, defaults));
        assertEquals(Collections.EMPTY_LIST, commandLine.getUndefaultedValues(option));
    }

    private Option createOption(final int maximumArguments) {
        final Argument argument = new ArgumentBuilder()
                .withName("value")
                .withMinimum(1)
                .withMaximum(maximumArguments)
                .create();

        return new DefaultOptionBuilder()
                .withShortName("v")
                .withArgument(argument)
                .create();
    }

    private CommandLine parse(final Option option, final List arguments) throws Exception {
        final Group group = new GroupBuilder()
                .withOption(option)
                .create();
        final Parser parser = new Parser();

        parser.setGroup(group);
        return parser.parse(arguments);
    }
}