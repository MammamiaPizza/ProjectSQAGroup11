import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

/**

 - Tests that demonstrate the CLI-203 bug:
 - <ul>
 - <li>{@link Parser#processProperties} must not throw {@link
org.apache.commons.cli.AlreadySelectedException}
 -    when a property supplies a second option from the same mutually exclusive group.</li>
 - <li>{@link DefaultParser#handleProperties} must not throw a NullPointerException when a property
key
 -    is not recognized as a registered option.</li>
 - </ul>
  */
 public class CLI203PropertiesTest extends TestCase {
  // ---- testPropertyOptionGroup (bug: AlreadySelectedException) ----
  public void testPropertyOptionGroup() {
  // Use both DefaultParser (own handleProperties) and BasicParser (inherits
Parser.processProperties)
  assertNoGroupExceptionForProperties(new DefaultParser());
  assertNoGroupExceptionForProperties(new BasicParser());
  }
  private void assertNoGroupExceptionForProperties(CommandLineParser parser) {
  Options options = new Options();
  OptionGroup group = new OptionGroup();
  Option optA = Option.builder("a").hasArg().build();
  Option optB = Option.builder("b").hasArg().build();
  group.addOption(optA);
  group.addOption(optB);
  options.addOptionGroup(group);
  // Two properties, both belonging to the same exclusive group.
  Properties props = new Properties();
  props.setProperty("a", "valueA");
  props.setProperty("b", "valueB");
  try {
      parser.parse(options, new String[0], props);
  } catch (ParseException e) {
      fail("Properties that select multiple options from the same group must not cause "
           + "AlreadySelectedException/ParseException: " + e.getClass() + " - " + e.getMessage());
  }
  }
  // ---- testPropertyOptionUnexpected (bug: NullPointerException) ----
  public void testPropertyOptionUnexpected() {
  assertUnexpectedPropertyNoNPE(new DefaultParser());
  assertUnexpectedPropertyNoNPE(new BasicParser());
  }
  private void assertUnexpectedPropertyNoNPE(CommandLineParser parser) {
  Options options = new Options();
  options.addOption("a", "alpha", false, "a flag");
  Properties props = new Properties();
  props.setProperty("unknownKey", "someValue");
  try {
      CommandLine cmd = parser.parse(options, new String[0], props);
      // The unrecognised property must not be added to the command line.
      assertFalse("Unrecognised property must not be registered as an option",
                  cmd.hasOption("unknownKey"));
  } catch (ParseException e) {
      fail("Unexpected property must never cause a ParseException (NPE fix): "
           + e.getClass() + " - " + e.getMessage());
  }
  }
  // ---- normal behaviour (sanity checks) ----
  public void testPropertyOptionNormal() {
  DefaultParser parser = new DefaultParser();
  Options options = new Options();
  Option opt = Option.builder("x").hasArg().build();
  options.addOption(opt);
  Properties props = new Properties();
  props.setProperty("x", "success");
  CommandLine cmd = null;
  try {
      cmd = parser.parse(options, new String[0], props);
  } catch (ParseException e) {
      fail("Normal property should parse successfully: " + e.getMessage());
  }
  assertEquals("Value from property must be retrieved", "success", cmd.getOptionValue("x"));
  }
  // ---- variant: property sets option already selected by CLI arg from same group ----
  public void testPropertyOptionGroupAfterArg() {
  // Configure a group with -a and -b
  Options options = new Options();
  OptionGroup group = new OptionGroup();
  Option optA = Option.builder("a").hasArg(false).build();
  Option optB = Option.builder("b").hasArg(false).build();
  group.addOption(optA);
  group.addOption(optB);
  options.addOptionGroup(group);
  // CLI argument selects -a
  String[] args = new String[] { "-a" };
  Properties props = new Properties();
  // Property tries to select -b (second option of the same group)
  props.setProperty("b", "true");
  DefaultParser parser = new DefaultParser();
  try {
      CommandLine cmd = parser.parse(options, args, props);
      // Should not throw; -a from args is already selected, property -b should be ignored.
      assertTrue("Option -a must be selected from CLI arg", cmd.hasOption("a"));
      assertFalse("Option -b from property must NOT be selected", cmd.hasOption("b"));
  } catch (ParseException e) {
      fail("Property from same group after CLI arg must not throw: " + e.getMessage());
  }
  }
  // ---- null properties ----
  public void testNullProperties() {
  DefaultParser parser = new DefaultParser();
  Options options = new Options();
  options.addOption("v", false, "verbose");
  try {
      CommandLine cmd = parser.parse(options, new String[0], (Properties) null);
      assertFalse(cmd.hasOption("v"));
  } catch (ParseException e) {
      fail("Null properties must be handled gracefully: " + e.getMessage());
  }
  BasicParser basic = new BasicParser();
  try {
      CommandLine cmd2 = basic.parse(options, new String[0], (Properties) null);
      assertFalse(cmd2.hasOption("v"));
  } catch (ParseException e) {
      fail("Null properties must be handled gracefully (BasicParser): " + e.getMessage());
  }
  }
  // ---- empty key/value in properties ----
  public void testEmptyKeyProperties() {
  DefaultParser parser = new DefaultParser();
  Options options = new Options();
  options.addOption("k", false, "a keyed option");
  Properties props = new Properties();
  props.setProperty("", "valueForEmptyKey");
  try {
      parser.parse(options, new String[0], props);
      // Empty key should be silently skipped, no NPE
  } catch (ParseException e) {
      fail("Empty property key must not cause ParseException: " + e.getMessage());
  }
  }
  public void testEmptyValueProperties() {
  DefaultParser parser = new DefaultParser();
  Options options = new Options();
  Option opt = Option.builder("d").hasArg().build();
  options.addOption(opt);
  Properties props = new Properties();
  props.setProperty("d", "");   // empty argument
  try {
      CommandLine cmd = parser.parse(options, new String[0], props);
      assertTrue(cmd.hasOption("d"));
      // empty string should be set as value
      assertEquals("", cmd.getOptionValue("d"));
  } catch (ParseException e) {
      fail("Empty property value must be accepted: " + e.getMessage());
  }
  }
  // ---- multiple unknown properties ----
  public void testMultipleUnknownProperties() {
  DefaultParser parser = new DefaultParser();
  Options options = new Options();
  options.addOption("x", false, "known");
  Properties props = new Properties();
  props.setProperty("ghost1", "val1");
  props.setProperty("ghost2", "val2");
  props.setProperty("ghost3", "val3");
  try {
      CommandLine cmd = parser.parse(options, new String[0], props);
      // none of the unknown keys should appear in the command line
      assertFalse(cmd.hasOption("ghost1"));
      assertFalse(cmd.hasOption("ghost2"));
      assertFalse(cmd.hasOption("ghost3"));
  } catch (ParseException e) {
      fail("Multiple unknown properties must not cause exception: " + e.getMessage());
  }
  }
  // ---- edge: option without argument, property value simply ignored ----
  public void testPropertyOptionWithNoArg() {
  DefaultParser parser = new DefaultParser();
  Options options = new Options();
  Option opt = Option.builder("f").hasArg(false).build();
  options.addOption(opt);
  Properties props = new Properties();
  props.setProperty("f", "someValueWillBeIgnored");
  try {
      CommandLine cmd = parser.parse(options, new String[0], props);
      assertTrue(cmd.hasOption("f"));
      // For a boolean flag, no value should be attached
      assertNull(cmd.getOptionValue("f"));
  } catch (ParseException e) {
      fail("Property for flag option must be handled: " + e.getMessage());
  }
  }

}