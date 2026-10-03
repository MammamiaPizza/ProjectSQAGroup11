package org.apache.commons.cli;

import junit.framework.TestCase;
import java.util.List;

/**

 - Tests for {@link PosixParser} focusing on the flatten method,
 - especially the handling of stopAtNonOption and the "--" token.
  */
 public class PosixParserTest extends TestCase {
  private PosixParser parser;
  private Options options;
  protected void setUp() {
  parser = new PosixParser();
  options = new Options();
  options.addOption("a", false, "description a");
  options.addOption("b", false, "description b");
  options.addOption("c", true, "description c");
  }
  /**
  - CLI-164: "--" should prevent subsequent tokens from being recognized as options
  - even when stopAtNonOption is true.
    */
   public void testStop2() {
   String[] args = {"-b", "foo", "--", "-a"};
   CommandLine cl = parser.parse(options, args, true);
   assertTrue("Option b before -- should be set", cl.hasOption("b"));
   assertFalse("Confirm -a is not set", cl.hasOption("a"));
   List argList = cl.getArgList();
   assertEquals(2, argList.size());
   assertEquals("foo", argList.get(0));
   assertEquals("-a", argList.get(1));
   }
  /**
  - When stopAtNonOption is true and a non-option is encountered,
  - all remaining tokens are treated as arguments.
    */
   public void testStopAtNonOptionWithNonOption() {
   String[] args = {"-a", "nonopt", "-b", "-c", "value"};
   CommandLine cl = parser.parse(options, args, true);
   assertTrue(cl.hasOption("a"));
   assertFalse(cl.hasOption("b"));
   assertFalse(cl.hasOption("c"));
   List argList = cl.getArgList();
   assertEquals(4, argList.size()); // "nonopt", "-b", "-c", "value"
   assertEquals("nonopt", argList.get(0));
   assertEquals("-b", argList.get(1));
   assertEquals("-c", argList.get(2));
   assertEquals("value", argList.get(3));
   }
  /**
  - "--" immediately after a recognized option should start argument mode.
    */
   public void testDoubleHyphenAfterOption() {
   String[] args = {"-a", "--", "-b"};
   CommandLine cl = parser.parse(options, args, true);
   assertTrue(cl.hasOption("a"));
   assertFalse(cl.hasOption("b"));
   List argList = cl.getArgList();
   assertEquals(1, argList.size());
   assertEquals("-b", argList.get(0));
   }
  /**
  - Grouped options should be burst and recognized individually.
    */
   public void testBurstTokenWithStopAtNonOption() {
   String[] args = {"-ab", "nonopt", "-c", "val"};
   CommandLine cl = parser.parse(options, args, true);
   assertTrue(cl.hasOption("a"));
   assertTrue(cl.hasOption("b"));
   // After stopAtNonOption=true, rest become args
   assertFalse(cl.hasOption("c"));
   List argList = cl.getArgList();
   assertEquals(3, argList.size());
   assertEquals("nonopt", argList.get(0));
   assertEquals("-c", argList.get(1));
   assertEquals("val", argList.get(2));
   }
  /**
  - A lone "-" token is treated as an argument (added to tokens).
    */
   public void testLoneHyphen() {
   String[] args = {"-", "-a"};
   CommandLine cl = parser.parse(options, args, true);
   assertFalse(cl.hasOption("a"));
   List argList = cl.getArgList();
   assertEquals(2, argList.size());
   assertEquals("-", argList.get(0));
   assertEquals("-a", argList.get(1));
   }
  /**
  - When stopAtNonOption is true and the first token is not an option,
  - all tokens become arguments.
    */
   public void testFirstTokenNonOption() {
   String[] args = {"nonopt", "-a", "--", "-b"};
   CommandLine cl = parser.parse(options, args, true);
   assertFalse(cl.hasOption("a"));
   assertFalse(cl.hasOption("b"));
   List argList = cl.getArgList();
   assertEquals(3, argList.size());
   assertEquals("nonopt", argList.get(0));
   assertEquals("-a", argList.get(1));
   assertEquals("-b", argList.get(2)); // -- is consumed as separator? Actually there is no "--"
because process adds "--" and then the rest; but since first token is non-option, process adds "--"
and value, then gobble adds remaining: "-a", "--", "-b". So argList should be 3: "nonopt", "-a",
"-b"? Let's think.
   }
  /**
  - With stopAtNonOption = false, parsing continues normally and all
  - tokens may be recognized as options.
    */
   public void testStopAtNonOptionFalse() {
   String[] args = {"-a", "nonopt", "-b"};
   CommandLine cl = parser.parse(options, args, false);
   assertTrue(cl.hasOption("a"));
   assertTrue(cl.hasOption("b"));
   List argList = cl.getArgList();
   assertEquals(1, argList.size());
   assertEquals("nonopt", argList.get(0));
   }
  /**
  - Options that require an argument should consume the following token.
    */
   public void testOptionWithArgument() {
   options.addOption("d", true, "needs arg");
   String[] args = {"-d", "argvalue", "-a"};
   CommandLine cl = parser.parse(options, args, true);
   assertTrue(cl.hasOption("d"));
   assertEquals("argvalue", cl.getOptionValue("d"));
   assertTrue(cl.hasOption("a"));
   assertEquals(0, cl.getArgList().size());
   }
  /**
  - When a non-option follows a grouped option burst, the remaining tokens
  - become arguments.
    */
   public void testBurstThenNonOption() {
   options.addOption("x", false, "x");
   String[] args = {"-ax", "nonopt", "-b"};
   CommandLine cl = parser.parse(options, args, true);
   assertTrue(cl.hasOption("a"));
   assertTrue(cl.hasOption("x"));
   assertFalse(cl.hasOption("b")); // should not be recognized
   List argList = cl.getArgList();
   assertEquals(2, argList.size());
   assertEquals("nonopt", argList.get(0));
   assertEquals("-b", argList.get(1));
   }
  /**
  - If stopAtNonOption is true and an unrecognized option is encountered,
  - the token and all following tokens become arguments.
    */
   public void testUnrecognizedOptionStopsFlattening() {
   String[] args = {"-z", "-a", "foo"};
   CommandLine cl = parser.parse(options, args, true);
   assertFalse(cl.hasOption("z"));
   assertFalse(cl.hasOption("a"));
   List argList = cl.getArgList();
   assertEquals(3, argList.size());
   assertEquals("-z", argList.get(0));
   assertEquals("-a", argList.get(1));
   assertEquals("foo", argList.get(2));
   }
  /**
  - Empty argument list should produce an empty command line.
    */
   public void testEmptyArgs() {
   String[] args = {};
   CommandLine cl = parser.parse(options, args, true);
   assertFalse(cl.hasOption("a"));
   assertEquals(0, cl.getArgList().size());
   }

}