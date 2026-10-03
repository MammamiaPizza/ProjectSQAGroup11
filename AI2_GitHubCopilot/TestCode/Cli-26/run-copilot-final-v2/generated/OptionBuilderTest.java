package org.apache.commons.cli;

import junit.framework.TestCase;

/**

 - Tests for {@link OptionBuilder} reset behavior, especially the bug where
 - description (and other fields) leak between option creations.
  */
 public class OptionBuilderTest extends TestCase {
  /**
  - Core regression test: after creating an option with a description,
  - the next option created without setting a description must have a null
  - description. Bug manifests as "we inherited a description".
    */
   public void testBuilderIsResettedAlways() {
   OptionBuilder.withDescription("some description");
   Option first = OptionBuilder.create('a');
   assertEquals("some description", first.getDescription());
   // Builder should be fully reset; next option must not inherit the
   // description.
   Option second = OptionBuilder.create('b');
   assertNull("we inherited a description", second.getDescription());
   }
  /**
  - Builder reset must happen even when {@link OptionBuilder#create()}
  - throws because of a null longopt.
    */
   public void testBuilderIsResettedAfterFailedCreateNoLongopt() {
   OptionBuilder.withDescription("inherited description");
   try {
   OptionBuilder.create();
   fail("Expected IllegalArgumentException because longopt is null");
   } catch (IllegalArgumentException expected) {
   // expected
   }
   // After the exception the builder state must be clean.
   Option opt = OptionBuilder.create('x');
   assertNull("builder did not reset after failed create()", opt.getDescription());
   }
  /**
  - When {@link OptionBuilder#create()} is called with a longopt set
  - it delegates to {@code create(null)}. If the {@link Option} constructor
  - rejects a null opt the builder must still reset.
    */
   public void testBuilderIsResettedAfterNullOptCreate() {
   OptionBuilder.withDescription("leaked description").withLongOpt("long");
   // create() -> create(null) may throw; catch to verify reset.
   try {
   OptionBuilder.create();
   } catch (RuntimeException e) {
   // swallow – the point is that builder state should be clean
   }
   Option fresh = OptionBuilder.create('y');
   assertNull("description leaked after failed create(null)", fresh.getDescription());
   assertNull("longopt leaked after failed create(null)", fresh.getLongOpt());
   }
  /**
  - Test that longopt is properly reset.
    */
   public void testLongoptIsReset() {
   OptionBuilder.withLongOpt("my-long");
   Option first = OptionBuilder.create('c');
   assertEquals("my-long", first.getLongOpt());
   Option second = OptionBuilder.create('d');
   assertNull("longopt was not reset", second.getLongOpt());
   }
  /**
  - Test that required flag is properly reset.
    */
   public void testRequiredIsReset() {
   OptionBuilder.isRequired();
   Option first = OptionBuilder.create('e');
   assertTrue(first.isRequired());
   Option second = OptionBuilder.create('f');
   assertFalse("required flag was not reset", second.isRequired());
   }
  /**
  - Test that numberOfArgs is properly reset to UNINITIALIZED.
    */
   public void testNumberOfArgsIsReset() {
   OptionBuilder.hasArg(); // numberOfArgs = 1
   Option first = OptionBuilder.create('g');
   assertEquals(1, first.getArgs());
   Option second = OptionBuilder.create('h');
   // UNINITIALIZED == -1
   assertEquals("numberOfArgs was not reset", Option.UNINITIALIZED, second.getArgs());
   }
  /**
  - Test that argName is properly reset to "arg".
    */
   public void testArgNameIsReset() {
   OptionBuilder.withArgName("customArg");
   Option first = OptionBuilder.create('i');
   assertEquals("customArg", first.getArgName());
   Option second = OptionBuilder.create('j');
   assertEquals("arg", second.getArgName());
   }
  /**
  - Test that type is properly reset to null.
    */
   public void testTypeIsReset() {
   OptionBuilder.withType(Integer.class);
   Option first = OptionBuilder.create('k');
   assertEquals(Integer.class, first.getType());
   Option second = OptionBuilder.create('l');
   assertNull("type was not reset", second.getType());
   }
  /**
  - Test that valueSeparator is properly reset to (char) 0.
    */
   public void testValueSeparatorIsReset() {
   OptionBuilder.withValueSeparator(':');
   Option first = OptionBuilder.create('m');
   assertEquals(':', first.getValueSeparator());
   Option second = OptionBuilder.create('n');
   assertEquals("valueSeparator was not reset", (char) 0, second.getValueSeparator());
   }
  /**
  - Test that optionalArg is properly reset to false.
    */
   public void testOptionalArgIsReset() {
   OptionBuilder.hasOptionalArg();
   Option first = OptionBuilder.create('o');
   assertTrue(first.hasOptionalArg());
   Option second = OptionBuilder.create('p');
   assertFalse("optionalArg was not reset", second.hasOptionalArg());
   }
  /**
  - Verifies that chaining produces an option with correct description
  - and that a subsequent option does not inherit it.
    */
   public void testChainingDoesNotLeakDescription() {
   Option first = OptionBuilder.withDescription("first desc")
       .withLongOpt("first-long")
       .create('r');
   assertEquals("first desc", first.getDescription());
   // Create a new option without setting description.
   Option second = OptionBuilder.hasArg().create('s');
   assertNull("description leaked from chained call", second.getDescription());
   assertEquals(1, second.getArgs());
   }

}
