package org.apache.commons.cli;

import junit.framework.TestCase;

public class OptionBuilderResetTest extends TestCase {

    protected void setUp() throws Exception {
        OptionBuilder.create("reset");
    }

    protected void tearDown() throws Exception {
        OptionBuilder.create("reset");
    }

    public void testCreateStringResetsAllConfiguredProperties() {
        OptionBuilder.withLongOpt("first-long");
        OptionBuilder.withDescription("first description");
        OptionBuilder.withArgName("input");
        OptionBuilder.isRequired();
        OptionBuilder.hasOptionalArgs(3);
        OptionBuilder.withType(String.class);
        OptionBuilder.withValueSeparator(':');

        Option first = OptionBuilder.create("a");

        assertEquals("first-long", first.getLongOpt());
        assertEquals("first description", first.getDescription());
        assertEquals("input", first.getArgName());
        assertTrue(first.isRequired());
        assertEquals(3, first.getArgs());
        assertTrue(first.hasOptionalArg());
        assertSame(String.class, first.getType());
        assertEquals((int) ':', (int) first.getValueSeparator());

        Option second = OptionBuilder.create("b");

        assertNull(second.getLongOpt());
        assertNull(second.getDescription());
        assertEquals("arg", second.getArgName());
        assertFalse(second.isRequired());
        assertEquals(Option.UNINITIALIZED, second.getArgs());
        assertFalse(second.hasOptionalArg());
        assertNull(second.getType());
        assertEquals(0, (int) second.getValueSeparator());
    }

    public void testCreateCharResetsDescriptionAndLongOption() {
        OptionBuilder.withLongOpt("alpha");
        OptionBuilder.withDescription("description for alpha");

        Option first = OptionBuilder.create('a');
        Option second = OptionBuilder.create('b');

        assertEquals("alpha", first.getLongOpt());
        assertEquals("description for alpha", first.getDescription());
        assertNull(second.getLongOpt());
        assertNull(second.getDescription());
    }

    public void testCreateWithoutOptionNameResetsStateAfterSuccessfulBuild() {
        OptionBuilder.withLongOpt("verbose");
        OptionBuilder.withDescription("enable verbose output");
        OptionBuilder.isRequired();

        Option first = OptionBuilder.create();
        Option second = OptionBuilder.create("v");

        assertEquals("verbose", first.getLongOpt());
        assertEquals("enable verbose output", first.getDescription());
        assertTrue(first.isRequired());
        assertNull(second.getLongOpt());
        assertNull(second.getDescription());
        assertFalse(second.isRequired());
    }

    public void testCreateWithoutLongOptionThrowsAndClearsAccumulatedState() {
        OptionBuilder.withDescription("stale description");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();

        try {
            OptionBuilder.create();
            fail("create() without a long option should throw IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }

        Option option = OptionBuilder.create("x");

        assertNull(option.getDescription());
        assertFalse(option.isRequired());
        assertEquals(Option.UNINITIALIZED, option.getArgs());
    }
}