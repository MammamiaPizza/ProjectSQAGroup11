package org.apache.commons.cli;

 import junit.framework.TestCase;

 import java.util.Properties;

 /**
  * Tests for {@link Parser#processProperties(Properties)} focusing on the CLI-201 bug:
  * property-set boolean options should be recognized after parsing.
  */
 public class ParserProcessPropertiesTest extends TestCase {
     /**
      * A concrete Parser subclass for testing. Uses a simple flatten implementation.
      */
     private static class TestParser extends Parser {
         @Override
         protected String[] flatten(Options opts, String[] arguments, boolean stopAtNonOption) {
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

     public void testPropertySetsBooleanOptionToTrue() throws Exception {
         Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
         options.addOption(verbose);
         Properties props = new Properties();
         props.setProperty("verbose", "true");
         CommandLine cmd = parser.parse(options, new String[0], props);
         assertTrue("Boolean option should be true when property value is 'true'",
                 cmd.hasOption("verbose"));
     }

     public void testPropertyWithNullValueSetsBooleanOption() throws Exception {
         Option debug = OptionBuilder.withLongOpt("debug").create("d");
         options.addOption(debug);
         Properties props = new Properties();
         cmd = parser.parse(options, new String[0], props);
         assertFalse("Boolean option should NOT be true when property key does not exist",
                 cmd.hasOption("debug"));
     }

     public void testPropertyWithEmptyValueSetsBooleanOption() throws Exception {
         Option force = OptionBuilder.withLongOpt("force").create("f");
         options.addOption(force);
         Properties props = new Properties();
         props.setProperty("force", "");
         CommandLine cmd = parser.parse(options, new String[0], props);
         assertTrue("Boolean option should be true when property value is empty",
                 cmd.hasOption("force"));
     }

     public void testPropertyWithYesValueSetsBooleanOption() throws Exception {
         Option debug = OptionBuilder.withLongOpt("debug").create("d");
         options.addOption(debug);
         Properties props = new Properties();
         props.setProperty("debug", "yes");
         CommandLine cmd = parser.parse(options, new String[0], props);
         assertTrue("Boolean option should be true for any non-false-like property value",
                 cmd.hasOption("debug"));
     }

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

     public void testPropertyWithFalseValueOnBooleanOption() throws Exception {
         Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
         options.addOption(verbose);
         Properties props = new Properties();
         props.setProperty("verbose", "false");
         CommandLine cmd = parser.parse(options, new String[0], props);
         assertTrue("Boolean option with 'false' value: existence of key may still set it",
                 cmd.hasOption("verbose"));
     }

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

     public void testUnrecognizedPropertyKeyIgnored() throws Exception {
         Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
         options.addOption(verbose);
         Properties props = new Properties();
         props.setProperty("nonexistent", "true");
         CommandLine cmd = parser.parse(options, new String[0], props);
         assertFalse("Unrecognized property should not set option", cmd.hasOption("nonexistent"));
         assertFalse("Unrelated option should not be affected unless its property is set",
                 cmd.hasOption("verbose"));
     }

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
         assertEquals("Output value should match property", "/dev/null",
cmd.getOptionValue("output"));
     }

     public void testNullPropertiesDoesNotThrow() throws Exception {
         Option verbose = OptionBuilder.withLongOpt("verbose").create("v");
         options.addOption(verbose);
         CommandLine cmd = parser.parse(options, new String[0], null);
         assertFalse("Option should not be set with null properties", cmd.hasOption("verbose"));
     }
 }
