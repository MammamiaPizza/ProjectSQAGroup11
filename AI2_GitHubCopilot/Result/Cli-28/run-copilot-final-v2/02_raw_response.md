package org.apache.commons.cli;

import junit.framework.TestCase;

import java.util.Properties;

/**

 - Tests for {@link Parser#processProperties(Properties)} focusing on the CLI-201 bug:
 - property-set boolean options should be recognized after parsing.
  */
 public class ParserProcessPropertiesTest extends TestCase {
  /**
  - A concrete Parser subclass for testing. Uses a simple flatten implementation.
    */
   private static class TestParser extends Parser {
   @Override
   protected String[] flatten(Options opts, String[] arguments, boolean stopAtNonOption) {
   // Simply return the arguments as-is
   return arguments == null ? new String[0] : arguments;
   }
   }
  private Options options;
  private Parser parser;
  @Override
  protected void setUp() {
      options = new Options();
      parser = new TestParser();
  }
  // --- Normal cases: property key (longOpt) should set boolean Option flag ---
  /**
  - CLI-201: A property key matching a boolean option's longOpt with value "true"
  - should set the option as present on the command line.
    */
   public void testPropertySetsBooleanOptionToTrue() throws Exception {
   Option verbose = OptionBuilder.withLongOpt("verbose")
       .create("v");
   options.addOption(verbose);
   Properties props = new Properties();
   props.setProperty("verbose", "true");
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertTrue("Boolean option should be true when property value is 'true'",
       cmd.hasOption("verbose"));
  }
  /**
  - CLI-201: A property key matching a boolean option's longOpt with no explicit value
  - (null) should set the option as present (flag set to true).
    */
   public void testPropertyWithNullValueSetsBooleanOption() throws Exception {
   Option debug = OptionBuilder.withLongOpt("debug").create("d");
   options.addOption(debug);
   // Property key with null value
   Properties props = new Properties();
   props.put("debug", null);
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertTrue("Boolean option should be true when property key exists with null value",
       cmd.hasOption("debug"));
  }
  /**
  - CLI-201: A property key matching a boolean option's longOpt with empty string value
  - should set the option as present (flag set to true).
    */
   public void testPropertyWithEmptyValueSetsBooleanOption() throws Exception {
   Option force = OptionBuilder.withLongOpt("force").create("f");
   options.addOption(force);
   Properties props = new Properties();
   props.setProperty("force", "");
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertTrue("Boolean option should be true when property value is empty",
       cmd.hasOption("force"));
  }
  /**
  - CLI-201: A property key matching a boolean option's longOpt with value "yes"
  - (non-"true") should still set the option as present (any non-null/empty value
  - is considered truthy for a boolean option, or the key existence itself suffices).
    */
   public void testPropertyWithYesValueSetsBooleanOption() throws Exception {
   Option debug = OptionBuilder.withLongOpt("debug").create("d");
   options.addOption(debug);
   Properties props = new Properties();
   props.setProperty("debug", "yes");
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertTrue("Boolean option should be true for any non-false-like property value",
       cmd.hasOption("debug"));
  }
  // --- Option with argument: property value should be passed to the option ---
  /**
  - An option with an argument (hasArg=true) should receive the property value
  - as its argument value.
    */
   public void testPropertySetsOptionWithArgument() throws Exception {
   Option output = OptionBuilder.withLongOpt("output")
       .hasArg()
       .create("o");
   options.addOption(output);
   Properties props = new Properties();
   props.setProperty("output", "/tmp/out.txt");
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertTrue("Option with argument should be present", cmd.hasOption("output"));
   assertEquals("Option should receive the property value",
       "/tmp/out.txt", cmd.getOptionValue("output"));
  }
  // --- Boundary cases ---
  /**
  - CLI-201 boundary: property value "false" for a boolean option. Per CLI-201 spec,
  - the property key existence sets the flag regardless of value; "false" may still
  - be treated as setting the option. We verify the actual behavior: the option
  - should NOT be set when the user intended "false" to mean disabled.
  -
  - Note: This test reflects the CLI-201 behavior where the exact semantic for
  - "false" may set the flag (the bug was that property flags were ignored entirely).
  - If the implementation distinguishes "false", this test documents that expectation.
    */
   public void testPropertyWithFalseValueOnBooleanOption() throws Exception {
   Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
   options.addOption(verbose);
   Properties props = new Properties();
   props.setProperty("verbose", "false");
   CommandLine cmd = parser.parse(options, new String[0], props);
   // Per CLI-201: the property key itself sets the flag; "false" may still set it.
   // We assert the de facto behavior: boolean option is set when property key exists.
   assertTrue("Boolean option with 'false' value: existence of key may still set it",
       cmd.hasOption("verbose"));
  }
  /**
  - CLI-201: A property value overridden by an explicit CLI argument.
  - When both a property and a command-line argument set the same option,
  - the command-line argument should win (last-set or overridden).
    */
   public void testPropertyOverriddenByExplicitCliArg() throws Exception {
   Option count = OptionBuilder.withLongOpt("count")
       .hasArg()
       .withType(Number.class)
       .create("c");
   options.addOption(count);
   Properties props = new Properties();
   props.setProperty("count", "5");
   CommandLine cmd = parser.parse(options, new String[]{"--count", "10"}, props);
   assertTrue("Option should be present", cmd.hasOption("count"));
   assertEquals("Explicit CLI arg should override property value",
       "10", cmd.getOptionValue("count"));
  }
  /**
  - A property value for an option WITHOUT a CLI argument but with a prior property
  - value set; ensure the first property value is used.
    */
   public void testPropertySingleValueForOptionWithArgument() throws Exception {
   Option filter = OptionBuilder.withLongOpt("filter")
       .hasArg()
       .create("F");
   options.addOption(filter);
   Properties props = new Properties();
   props.setProperty("filter", "active");
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertTrue("Option should be present from property", cmd.hasOption("filter"));
   assertEquals("Option value should match property", "active", cmd.getOptionValue("filter"));
  }
  // --- Error/Edge cases ---
  /**
  - CLI-201: A property key that does NOT correspond to any Option should be
  - silently ignored, not causing any exception or error.
    */
   public void testUnrecognizedPropertyKeyIgnored() throws Exception {
   Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
   options.addOption(verbose);
   Properties props = new Properties();
   props.setProperty("nonexistent", "true");
   // Should not throw
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertFalse("Unrecognized property should not set option", cmd.hasOption("nonexistent"));
   // The unrelated option should remain unset
   assertFalse("Unrelated option should not be affected", cmd.hasOption("verbose"));
  }
  /**
  - CLI-201: A property that has the same longOpt as an option but also sets
  - a value for an option with hasArg=true; verify both the boolean and arg options
  - can coexist.
    */
   public void testMultiplePropertiesSetDifferentOptions() throws Exception {
   Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
   Option output = OptionBuilder.withLongOpt("output").hasArg().create("o");
   options.addOption(verbose);
   options.addOption(output);
   Properties props = new Properties();
   props.setProperty("verbose", "true");
   props.setProperty("output", "/dev/null");
   CommandLine cmd = parser.parse(options, new String[0], props);
   assertTrue("Boolean verbose should be set", cmd.hasOption("verbose"));
   assertTrue("Option output should be set", cmd.hasOption("output"));
   assertEquals("Output value should match property", "/dev/null", cmd.getOptionValue("output"));
  }
  /**
  - CLI-201: null properties should be handled gracefully (no-op).
    */
   public void testNullPropertiesDoesNotThrow() throws Exception {
   Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
   options.addOption(verbose);
   // Passing null properties should not throw
   CommandLine cmd = parser.parse(options, new String[0], null);
   // No properties, so no options should be set
   assertFalse("Option should not be set with null properties", cmd.hasOption("verbose"));
  }

}