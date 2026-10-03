package org.apache.commons.cli;

import java.util.Collection;

import junit.framework.TestCase;

public class OptionGroupLongOptionTest extends TestCase
{
    public void testSelectingLongOnlyOptionStoresLongOptionName() throws Exception
    {
        OptionGroup group = new OptionGroup();
        Option bar = new Option(null, "bar", false, "bar option");

        group.addOption(bar);
        group.setSelected(bar);

        assertEquals("bar", group.getSelected());
    }

    public void testReselectingSameLongOnlyOptionIsAllowedButDifferentOptionIsRejected()
        throws Exception
    {
        OptionGroup group = new OptionGroup();
        Option bar = new Option(null, "bar", false, "bar option");
        Option foo = new Option(null, "foo", false, "foo option");

        group.addOption(bar);
        group.addOption(foo);

        group.setSelected(bar);
        group.setSelected(bar);

        assertEquals("bar", group.getSelected());

        try
        {
            group.setSelected(foo);
            fail("Selecting a second option in the group should fail");
        }
        catch (AlreadySelectedException expected)
        {
            assertEquals("bar", group.getSelected());
        }
    }

    public void testSelectingNullClearsPreviouslySelectedLongOnlyOption() throws Exception
    {
        OptionGroup group = new OptionGroup();
        Option bar = new Option(null, "bar", false, "bar option");

        group.addOption(bar);
        group.setSelected(bar);
        group.setSelected(null);

        assertNull(group.getSelected());
    }

    public void testLongOnlyOptionsAreExposedByNamesAndOptionsCollections()
    {
        OptionGroup group = new OptionGroup();
        Option bar = new Option(null, "bar", false, "bar option");
        Option foo = new Option(null, "foo", false, "foo option");

        group.addOption(bar);
        group.addOption(foo);

        Collection names = group.getNames();
        Collection options = group.getOptions();

        assertEquals(2, names.size());
        assertTrue(names.contains("bar"));
        assertTrue(names.contains("foo"));
        assertEquals(2, options.size());
        assertTrue(options.contains(bar));
        assertTrue(options.contains(foo));
    }

    public void testBasicParserSelectsLongOnlyOptionGroupMember() throws Exception
    {
        assertLongOptionGroupSelection(new BasicParser());
    }

    public void testGnuParserSelectsLongOnlyOptionGroupMember() throws Exception
    {
        assertLongOptionGroupSelection(new GnuParser());
    }

    public void testPosixParserSelectsLongOnlyOptionGroupMember() throws Exception
    {
        assertLongOptionGroupSelection(new PosixParser());
    }

    private void assertLongOptionGroupSelection(CommandLineParser parser) throws Exception
    {
        OptionGroup group = new OptionGroup();
        Option bar = new Option(null, "bar", false, "bar option");
        group.addOption(bar);

        Options options = new Options();
        options.addOptionGroup(group);

        CommandLine commandLine = parser.parse(options, new String[] { "--bar" });

        assertTrue(commandLine.hasOption("bar"));
        assertEquals("bar", group.getSelected());
    }
}