package org.apache.commons.cli;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**

 - Tests for HelpFormatter bug regarding empty argument names (Cli-11).
 - The bug causes " <>" to be printed for options with empty arg name.
  */
 public class HelpFormatterBug11Test extends TestCase {
  private HelpFormatter formatter;
  public HelpFormatterBug11Test(String name) {
  super(name);
  }
  protected void setUp() {
  formatter = new HelpFormatter();
  }
  public static Test suite() {
  TestSuite suite = new TestSuite();
  suite.addTest(new HelpFormatterBug11Test("testPrintOptionWithEmptyArgNameUsage"));
  suite.addTest(new HelpFormatterBug11Test("testNullArgNamePrintsNoArg"));
  suite.addTest(new HelpFormatterBug11Test("testNormalArgNamePrintsCorrectly"));
  suite.addTest(new HelpFormatterBug11Test("testOptionWithoutArg"));
  suite.addTest(new HelpFormatterBug11Test("testLongOptionWithEmptyArgName"));
  suite.addTest(new HelpFormatterBug11Test("testPrintUsageMultipleOptions"));
  suite.addTest(new HelpFormatterBug11Test("testPrintUsageRequiredOption"));
  suite.addTest(new HelpFormatterBug11Test("testPrintHelpNoException"));
  suite.addTest(new HelpFormatterBug11Test("testSetGetArgName"));
  suite.addTest(new HelpFormatterBug11Test("testPrintUsageWithChangedArgName"));
  suite.addTest(new HelpFormatterBug11Test("testPrintUsageSyntaxPrefix"));
  suite.addTest(new HelpFormatterBug11Test("testDefaultArgNameNotUsedWhenNull"));
  return suite;
  }
  // ------------------- helpers ------------------------------
  private String usageFor(Options options) {
  StringWriter sw = new StringWriter();
  PrintWriter pw = new PrintWriter(sw);
  formatter.printUsage(pw, 80, "app", options);
  pw.flush();
  return sw.toString().trim();
  }
  private String usageFor(Option option) {
  Options options = new Options();
  options.addOption(option);
  return usageFor(options);
  }
  // ------------------- test methods ------------------------------
  /**
  - Trigger: empty arg name should produce no argument placeholder.
  - Expected output: "usage: app -f" (no " <>")
    */
   public void testPrintOptionWithEmptyArgNameUsage() {
   Option opt = new Option("f", "file", true, "description");
   opt.setArgName("");
   String result = usageFor(opt);
   assertEquals("usage: app -f", result);
   }
  /**
  - Null arg name: hasArgName() returns false, no placeholder printed.
    */
   public void testNullArgNamePrintsNoArg() {
   Option opt = new Option("f", "file", true, "description");
   // argName is null by default
   String result = usageFor(opt);
   assertEquals("usage: app -f", result);
   }
  /**
  - Normal arg name prints in angle brackets.
    */
   public void testNormalArgNamePrintsCorrectly() {
   Option opt = new Option("f", "file", true, "description");
   opt.setArgName("file");
   String result = usageFor(opt);
   assertEquals("usage: app -f <file>", result);
   }
  /**
  - Option without argument: no placeholder at all.
    */
   public void testOptionWithoutArg() {
   Option opt = new Option("f", "file", false, "description");
   String result = usageFor(opt);
   assertEquals("usage: app -f", result);
   }
  /**
  - Long option with empty arg name (using only long opt).
    */
   public void testLongOptionWithEmptyArgName() {
   Option opt = new Option(null, "myopt", true, "desc");
   opt.setArgName("");
   String result = usageFor(opt);
   assertEquals("usage: app--myopt", result);
   }
  /**
  - Multiple options, one with empty arg name, one normal.
    */
   public void testPrintUsageMultipleOptions() {
   Option opt1 = new Option("f", null, true, "first");
   opt1.setArgName("");
   Option opt2 = new Option("o", "output", false, "second");
   Options options = new Options();
   options.addOption(opt1);
   options.addOption(opt2);
   String result = usageFor(options);
   // order by comparator (alphabetical short opt): -f then -o
   assertEquals("usage: app -f -o", result);
   }
  /**
  - Required option with empty arg name.
    */
   public void testPrintUsageRequiredOption() {
   Option opt = new Option("f", null, true, "forced");
   opt.setArgName("");
   opt.setRequired(true);
   String result = usageFor(opt);
   assertEquals("usage: app -f", result);
   }
  /**
  - Print help with a basic option – should not throw exceptions.
    */
   public void testPrintHelpNoException() {
   Options options = new Options();
   options.addOption(new Option("h", "help", false, "show help"));
   StringWriter sw = new StringWriter();
   PrintWriter pw = new PrintWriter(sw);
   try {
   formatter.printHelp(pw, 80, "app", null, options, 1, 3, null, true);
   pw.flush();
   } catch (Exception e) {
   fail("printHelp should not throw: " + e.getMessage());
   }
   assertTrue("usage prefix expected", sw.toString().trim().startsWith("usage: app"));
   }
  /**
  - Set and get default arg name.
    */
   public void testSetGetArgName() {
   assertEquals("arg", formatter.getArgName());
   formatter.setArgName("value");
   assertEquals("value", formatter.getArgName());
   formatter.setArgName(null);
   assertNull(formatter.getArgName());
   }
  /**
  - Changing the formatter's default arg name does not affect
  - printUsage when option's arg name is empty (still no placeholder).
    */
   public void testPrintUsageWithChangedArgName() {
   formatter.setArgName("PARAM");
   Option opt = new Option("f", null, true, "desc");
   opt.setArgName("");
   String result = usageFor(opt);
   assertEquals("usage: app -f", result);
   }
  /**
  - Verify the default syntax prefix is used.
    */
   public void testPrintUsageSyntaxPrefix() {
   Option opt = new Option("v", false, "verbose");
   String result = usageFor(opt);
   assertTrue("should start with 'usage: '", result.startsWith("usage: "));
   formatter.setSyntaxPrefix("synopsis: ");
   result = usageFor(opt);
   assertTrue("should start with 'synopsis: '", result.startsWith("synopsis: "));
   }
  /**
  - When option has argument but hasArgName() is false (null),
  - the formatter's defaultArgName is NOT used by printUsage.
    */
   public void testDefaultArgNameNotUsedWhenNull() {
   formatter.setArgName("myarg");
   Option opt = new Option("p", null, true, "val");
   // argName remains null, hasArgName() == false
   String result = usageFor(opt);
   assertEquals("usage: app -p", result);
   }

}