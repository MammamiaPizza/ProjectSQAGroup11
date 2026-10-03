package org.apache.commons.cli.bug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.AlreadySelectedException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CLI266OrderingTest {

    private Option option(String opt) {
        return new Option(opt, null, false, opt);
    }

    private List<String> keys(Collection<Option> options) {
        List<String> keys = new ArrayList<String>();
        for (Option option : options) {
            keys.add(option.getKey());
        }
        return keys;
    }

    @Test
    public void optionGroupNamesPreserveOptionInsertionOrder() {
        OptionGroup group = new OptionGroup();
        group.addOption(option("p"));
        group.addOption(option("x"));

        assertEquals(Arrays.asList("p", "x"), new ArrayList<String>(group.getNames()));
    }

    @Test
    public void optionGroupOptionsPreserveOptionInsertionOrder() {
        Option p = option("p");
        Option x = option("x");
        OptionGroup group = new OptionGroup();

        group.addOption(p);
        group.addOption(x);

        assertEquals(Arrays.asList("p", "x"), keys(group.getOptions()));
    }

    @Test
    public void optionsAddedFromGroupRetainGroupOrderAndMappings() {
        Option p = option("p");
        Option x = option("x");
        OptionGroup group = new OptionGroup();
        group.addOption(p);
        group.addOption(x);

        Options options = new Options();
        options.addOptionGroup(group);

        assertEquals(Arrays.asList("p", "x"), keys(options.getOptions()));
        assertSame(p, options.getOption("p"));
        assertSame(x, options.getOption("-x"));
        assertSame(group, options.getOptionGroup(p));
        assertSame(group, options.getOptionGroup(x));
    }

    @Test
    public void emptyOptionGroupHasNoNamesOrOptions() {
        OptionGroup group = new OptionGroup();

        assertTrue(group.getNames().isEmpty());
        assertTrue(group.getOptions().isEmpty());
    }

    @Test
    public void selectingDifferentOptionIsRejectedUntilSelectionIsReset() throws Exception {
        Option p = option("p");
        Option x = option("x");
        OptionGroup group = new OptionGroup();
        group.addOption(p);
        group.addOption(x);

        group.setSelected(p);
        assertEquals("p", group.getSelected());

        try {
            group.setSelected(x);
            fail("Selecting a different option in an OptionGroup must fail");
        } catch (AlreadySelectedException expected) {
            assertEquals("p", group.getSelected());
        }

        group.setSelected(null);
        group.setSelected(x);
        assertEquals("x", group.getSelected());
    }
}
