import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;

public class Cli16FaultTest extends TestCase {

    private int nextOptionId;

    private Option createOption(final String shortName, final String longName) {
        return new DefaultOptionBuilder()
                .withId(++nextOptionId)
                .withShortName(shortName)
                .withLongName(longName)
                .create();
    }

    public void testGetOptionsRetainsAllAddedOptionsInInsertionOrder() {
        final Option help = createOption("h", "help");
        final Option login = createOption("l", "login");
        final Option target = createOption("t", "target");

        final Option root = new GroupBuilder()
                .withOption(help)
                .withOption(login)
                .withOption(target)
                .create();

        final WriteableCommandLineImpl commandLine =
                new WriteableCommandLineImpl(root, new ArrayList());

        commandLine.addOption(help);
        commandLine.addOption(login);
        commandLine.addOption(target);

        final List options = commandLine.getOptions();

        assertEquals(3, options.size());
        assertSame(help, options.get(0));
        assertSame(login, options.get(1));
        assertSame(target, options.get(2));
    }

    public void testAddedOptionsAreAvailableByBothShortAndLongTriggers() {
        final Option help = createOption("h", "help");
        final Option login = createOption("l", "login");

        final Option root = new GroupBuilder()
                .withOption(help)
                .withOption(login)
                .create();

        final WriteableCommandLineImpl commandLine =
                new WriteableCommandLineImpl(root, new ArrayList());

        commandLine.addOption(help);
        commandLine.addOption(login);

        assertSame(help, commandLine.getOption("-h"));
        assertSame(help, commandLine.getOption("--help"));
        assertSame(login, commandLine.getOption("-l"));
        assertSame(login, commandLine.getOption("--login"));
        assertNull(commandLine.getOption("--missing"));
    }

    public void testGroupFindOptionResolvesChildOptionsAndRejectsUnknownTrigger() {
        final Option help = createOption("h", "help");
        final Option login = createOption("l", "login");

        final Option group = new GroupBuilder()
                .withOption(help)
                .withOption(login)
                .create();

        assertSame(help, group.findOption("--help"));
        assertSame(login, group.findOption("-l"));
        assertNull(group.findOption("--unknown"));
    }

    public void testOptionFindOptionResolvesOnlyItsOwnTrigger() {
        final Option help = createOption("h", "help");

        assertSame(help, help.findOption("-h"));
        assertSame(help, help.findOption("--help"));
        assertNull(help.findOption("--login"));
    }
}