package org.apache.commons.cli;

import junit.framework.TestCase;

/**

 - Test class for PosixParser, focusing on unrecognized option detection.
 - Exposes CLI-164: PosixParser does not throw UnrecognizedOptionException.
  */
 public class PosixParserTest extends TestCase {
  private Options options;
  public void setUp() {
  options = new Options();
  options.addOption(new Option("a", "alpha", false, "opt a"));
  options.addOption(new Option("b", false, "opt b"));
  Option optC = new Option("c", "charlie", true, "opt c with arg");
  options.addOption(optC);
  }
  public void testUnrecognizedShortOption() {
  PosixParser parser = new PosixParser();
  try {
      parser.parse(options, new String[]{"-z"});
      fail("UnrecognizedOptionException should have been thrown");
  } catch (UnrecognizedOptionException e) {
      // expected
  } catch (Exception e) {
      fail("Wrong exception type: " + e.getClass().getName());
  }
  }
  public void testUnrecognizedLongOption() {
  PosixParser parser = new PosixParser();
  try {
      parser.parse(options, new String[]{"--unknown"});
      fail("UnrecognizedOptionException should have been thrown");
  } catch (UnrecognizedOptionException e) {
      // expected
  } catch (Exception e) {
      fail("Wrong exception type: " + e.getClass().getName());
  }
  }
  public void testUnrecognizedShortAfterNonOption() {
  PosixParser parser = new PosixParser();
  try {
      parser.parse(options, new String[]{"foo", "-z"});
      fail("UnrecognizedOptionException should have been thrown");
  } catch (UnrecognizedOptionException e) {
      // expected
  } catch (Exception e) {
      fail("Wrong exception type: " + e.getClass().getName());
  }
  }
  public void testUnrecognizedLongAfterNonOption() {
  PosixParser parser = new PosixParser();
  try {
      parser.parse(options, new String[]{"foo", "--bad"});
      fail("UnrecognizedOptionException should have been thrown");
  } catch (UnrecognizedOptionException e) {
      // expected
  } catch (Exception e) {
      fail("Wrong exception type: " + e.getClass().getName());
  }
  }
  public void testUnrecognizedWithStopAtNonOptionTrue() {
  PosixParser parser = new PosixParser();
  try {
      CommandLine cl = parser.parse(options, new String[]{"-9"}, true);
      // stopAtNonOption treats unrecognized options as non-option arguments
      assertNotNull(cl);
      assertEquals(1, cl.getArgs().length);
      assertEquals("-9", cl.getArgs()[0]);
  } catch (ParseException e) {
      fail("Unexpected ParseException: " + e.getMessage());
  }
  }
  public void testUnrecognizedLongWithStopAtNonOptionTrue() {
  PosixParser parser = new PosixParser();
  try {
      CommandLine cl = parser.parse(options, new String[]{"--ghost"}, true);
      // stopAtNonOption treats unrecognized options as non-option arguments
      assertNotNull(cl);
      assertEquals(1, cl.getArgs().length);
      assertEquals("--ghost", cl.getArgs()[0]);
  } catch (ParseException e) {
      fail("Unexpected ParseException: " + e.getMessage());
  }
  }
  public void testUnrecognizedBurstToken() {
  PosixParser parser = new PosixParser();
  try {
      // "-xz": -x unknown, -z unknown, should cause exception
      parser.parse(options, new String[]{"-xz"});
      fail("UnrecognizedOptionException should have been thrown for burst token");
  } catch (UnrecognizedOptionException e) {
      // expected
  } catch (Exception e) {
      fail("Wrong exception type: " + e.getClass().getName());
  }
  }
  public void testUnrecognizedLongWithEquals() {
  PosixParser parser = new PosixParser();
  try {
      parser.parse(options, new String[]{"--bad=value"});
      fail("UnrecognizedOptionException should have been thrown for --bad=value");
  } catch (UnrecognizedOptionException e) {
      // expected
  } catch (Exception e) {
      fail("Wrong exception type: " + e.getClass().getName());
  }
  }
  public void testValidShortOption() {
  PosixParser parser = new PosixParser();
  try {
      CommandLine cl = parser.parse(options, new String[]{"-a"});
      assertTrue(cl.hasOption("a"));
      assertEquals(0, cl.getArgs().length);
  } catch (ParseException e) {
      fail("Unexpected ParseException: " + e.getMessage());
  }
  }
  public void testValidLongOption() {
  PosixParser parser = new PosixParser();
  try {
      CommandLine cl = parser.parse(options, new String[]{"--alpha"});
      assertTrue(cl.hasOption("alpha"));
      assertEquals(0, cl.getArgs().length);
  } catch (ParseException e) {
      fail("Unexpected ParseException: " + e.getMessage());
  }
  }
  public void testOptionWithArgument() {
  PosixParser parser = new PosixParser();
  try {
      CommandLine cl = parser.parse(options, new String[]{"-c", "value"});
      assertTrue(cl.hasOption("c"));
      assertEquals("value", cl.getOptionValue("c"));
  } catch (ParseException e) {
      fail("Unexpected ParseException: " + e.getMessage());
  }
  }
  public void testMissingArgumentException() {
  PosixParser parser = new PosixParser();
  try {
      parser.parse(options, new String[]{"-c"});
      fail("MissingArgumentException should have been thrown");
  } catch (MissingArgumentException e) {
      // expected
  } catch (Exception e) {
      fail("Wrong exception type: " + e.getClass().getName());
  }
  }

}