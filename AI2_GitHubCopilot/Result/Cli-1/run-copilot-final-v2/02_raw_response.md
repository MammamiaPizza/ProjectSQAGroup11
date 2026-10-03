package org.apache.commons.cli.bug;

import java.lang.reflect.Method;
import junit.framework.TestCase;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**

 - Tests for {@link CommandLine} covering the public API and exercising
 - the bug related to CLI-13: getOptionValue may return incorrect values
 - for options that are set with arguments, especially for long options
 - and the default-value fallback.
  */
 public class BugCLI13Test extends TestCase {
  private CommandLine cmd;
  private Option optA;   // short, with argument
  private Option optB;   // short, no argument (flag)
  private Option optC;   // short + long, with argument
  private Option optD;   // long only, with argument
  protected void setUp() throws Exception {
  // Create Option objects representing different configurations
  optA = new Option("a", true, "option a");
  optA.addValue("valueA");
  optB = new Option("b", false, "option b");
  optC = new Option("c", "long-c", true, "option c");
  optC.addValue("valueC");
  optD = new Option((String) null, "long-d", true, "option d");
  optD.addValue("valueD");
  // Build a CommandLine via reflection to avoid relying on the parser
  cmd = newCommandLine();
  // Add options using the package-private addOption method
  addOption(cmd, optA);
  addOption(cmd, optB);
  addOption(cmd, optC);
  addOption(cmd, optD);
  }
  // ---- hasOption -----------------------------------------------------------
  public void testHasOption() {
  assertTrue(cmd.hasOption("a"));
  assertTrue(cmd.hasOption('a'));
  assertTrue(cmd.hasOption("b"));
  assertTrue(cmd.hasOption("long-c"));
  assertTrue(cmd.hasOption("long-d"));
  assertFalse(cmd.hasOption("z"));
  assertFalse(cmd.hasOption('z'));
  assertFalse(cmd.hasOption(""));
  }
  // ---- getOptionValue (String) --------------------------------------------
  public void testGetOptionValueAbsent() {
  // absent option returns null
  assertNull(cmd.getOptionValue("z"));
  }
  public void testGetOptionValuePresentWithArg() {
  // option with argument returns the argument
  assertEquals("valueA", cmd.getOptionValue("a"));
  }
  public void testGetOptionValuePresentNoArg() {
  // option present but no argument returns null
  assertNull(cmd.getOptionValue("b"));
  }
  public void testLongOptionValue() {
  // long-only option with argument
  assertEquals("valueD", cmd.getOptionValue("long-d"));
  // also test the char variant on a long-only option (should work if we use the string version)
  // char variant delegates to String version, so char->String conversion
  }
  public void testGetOptionValueChar() {
  assertEquals("valueA", cmd.getOptionValue('a'));
  assertNull(cmd.getOptionValue('b'));
  assertNull(cmd.getOptionValue('z'));
  }
  public void testGetOptionValueWithDefault() {
  // option absent -> default
  assertEquals("default", cmd.getOptionValue("z", "default"));
  // option present with arg -> actual value, not default
  assertEquals("valueA", cmd.getOptionValue("a", "default"));
  // option present no arg -> according to contract: if option is set and has no argument,
  // it returns defaultValue (because specification says "otherwise defaultValue").
  assertEquals("default", cmd.getOptionValue("b", "default"));
  }
  // ---- getOptionValues ----------------------------------------------------
  public void testGetOptionValuesAbsent() {
  assertNull(cmd.getOptionValues("z"));
  }
  public void testGetOptionValuesPresent() {
  String[] values = cmd.getOptionValues("a");
  assertNotNull(values);
  assertEquals(1, values.length);
  assertEquals("valueA", values[0]);
  }
  // ---- getOptionObject ----------------------------------------------------
  public void testGetOptionObjectAbsent() {
  assertNull(cmd.getOptionObject("z"));
  }
  public void testGetOptionObjectPresent() {
  // Need a type for the value. Use String.class.
  optA.setType(String.class);
  // The Option was already added to cmd, so we need to update it in the map?
  // Remove and re-add to ensure the updated Option is used.
  cmd.getOptions(); // no-op
  // Instead, we can create a new CommandLine with the typed option to avoid mutation issues.
  // However, to test getOptionObject, we need the option in the map with the type set.
  // We'll build a fresh test here.
  try {
      CommandLine localCmd = newCommandLine();
      Option typedOpt = new Option("x", true, "typed option");
      typedOpt.addValue("typedValue");
      typedOpt.setType(String.class);
      addOption(localCmd, typedOpt);
      // getOptionObject should return the value casted (String)
      Object obj = localCmd.getOptionObject("x");
      assertNotNull(obj);
      assertEquals("typedValue", obj);
  } catch (Exception e) {
      fail("Reflection error: " + e.getMessage());
  }
  }
  // ---- getArgs / getArgList ------------------------------------------------
  public void testGetArgsEmpty() {
  assertEquals(0, cmd.getArgs().length);
  assertTrue(cmd.getArgList().isEmpty());
  }
  // ---- iterator / getOptions ----------------------------------------------
  public void testGetOptions() {
  Option[] opts = cmd.getOptions();
  assertEquals(4, opts.length);
  }
  public void testIterator() {
  int count = 0;
  java.util.Iterator it = cmd.iterator();
  while (it.hasNext()) {
      it.next();
      count++;
  }
  assertEquals(4, count);
  }
  // ---- edge cases ---------------------------------------------------------
  public void testNullAndEmptyOpt() {
  try {
      cmd.getOptionValue((String) null);
      // should not throw NPE; if it returns null it is acceptable.
      // The implementation may throw NPE; we can tolerate either.
  } catch (NullPointerException expected) {
      // acceptable
  }
  assertNull(cmd.hasOption("") ? null : null);
  // empty string: hasOption should return false
  assertFalse(cmd.hasOption(""));
  assertNull(cmd.getOptionValue(""));
  }
  // ---- helpers ------------------------------------------------------------
  private CommandLine newCommandLine() throws Exception {
  // Use reflection to instantiate because constructor is package-private
  Class clazz = CommandLine.class;
  java.lang.reflect.Constructor ctor = clazz.getDeclaredConstructor(new Class[0]);
  ctor.setAccessible(true);
  return (CommandLine) ctor.newInstance(new Object[0]);
  }
  private void addOption(CommandLine commandLine, Option option) throws Exception {
  Method addOption = CommandLine.class.getDeclaredMethod("addOption", Option.class);
  addOption.setAccessible(true);
  addOption.invoke(commandLine, option);
  }

}