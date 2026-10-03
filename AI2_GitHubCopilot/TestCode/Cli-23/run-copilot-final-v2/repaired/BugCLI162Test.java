package org.apache.commons.cli.bug;

import junit.framework.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**

 - Test suite for bug CLI-162: infinite loop when a token exceeds the wrapping width.
 - Covers findWrapPos, printWrapped, renderOptions, and related error handling.
  */
 public class BugCLI162Test extends TestCase {
  // Helper to expose protected methods
  private static class TestableHelpFormatter extends HelpFormatter {
  public int findWrapPosWrapper(String text, int width, int startPos) {
      return super.findWrapPos(text, width, startPos);
  }
  public StringBuffer renderWrappedTextWrapper(StringBuffer sb, int width,
                                                int nextLineTabStop, String text) {
      return super.renderWrappedText(sb, width, nextLineTabStop, text);
  }
  public StringBuffer renderOptionsWrapper(StringBuffer sb, int width,
                                            Options options, int leftPad, int descPad) {
      return super.renderOptions(sb, width, options, leftPad, descPad);
  }
  }
  // --- findWrapPos tests -------------------------------------------------
  public void testFindWrapPosFindsSpace() {
  TestableHelpFormatter hf = new TestableHelpFormatter();
  // "hello world" → space at index 5, width 10 → pos=5
  assertEquals(5, hf.findWrapPosWrapper("hello world", 10, 0));
  }
  public void testFindWrapPosLongTokenReturnsMinusOne() {
  TestableHelpFormatter hf = new TestableHelpFormatter();
  try {
      hf.findWrapPosWrapper("VeryLongUnbreakableToken", 5, 0);
      fail("RuntimeException expected");
  } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("CLI-162"));
  }
  }
  public void testFindWrapPosFindsNewLine() {
  TestableHelpFormatter hf = new TestableHelpFormatter();
  // newline within width → returns index after newline
  String text = "ab\ncd";
  // position 2 is '\n', position+1 = 3
  assertEquals(3, hf.findWrapPosWrapper(text, 5, 0));
  }
  // --- printWrapped tests ------------------------------------------------
  public void testPrintWrappedNormal() {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(baos);
  HelpFormatter hf = new HelpFormatter();
  hf.printWrapped(pw, 10, "hello world test");
  pw.flush();
  String result = baos.toString();
  assertFalse(result.contains("Infinitie") || result.contains("CLI-162"));
  assertTrue(result.startsWith("hello"));
  assertTrue(result.contains("world"));
  }
  public void testPrintWrappedLongTokenThrows() {
  HelpFormatter hf = new HelpFormatter();
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(baos);
  try {
      hf.printWrapped(pw, 10, "looooongdescription");
      fail("RuntimeException expected");
  } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("CLI-162"));
  }
  }
  public void testPrintWrappedWithTabStop() {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(baos);
  HelpFormatter hf = new HelpFormatter();
  // width=10, nextLineTabStop=2: continuation lines indented by 2 spaces
  hf.printWrapped(pw, 10, 2, "hello world again");
  pw.flush();
  String output = baos.toString();
  // should contain at least two line separators
  assertTrue(output.indexOf(System.getProperty("line.separator")) >= 0);
  }
  public void testPrintWrappedEmptyText() {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  PrintWriter pw = new PrintWriter(baos);
  HelpFormatter hf = new HelpFormatter();
  // empty text should not throw
  hf.printWrapped(pw, 10, "");
  pw.flush();
  assertEquals(System.getProperty("line.separator"), baos.toString());
  }
  public void testPrintWrappedNullText() {
  HelpFormatter hf = new HelpFormatter();
  PrintWriter pw = new PrintWriter(new ByteArrayOutputStream());
  try {
      hf.printWrapped(pw, 10, (String) null);
      fail("NullPointerException expected");
  } catch (NullPointerException expected) {
      // expected
  }
  }
  // --- Option rendering / printHelp tests --------------------------------
  public void testPrintHelpLongDescriptionThrows() {
  Options opts = new Options();
  Option opt = new Option("a", "looooongoptiondescription");
  opts.addOption(opt);
  HelpFormatter hf = new HelpFormatter();
  hf.setWidth(10);
  StringWriter sw = new StringWriter();
  PrintWriter pw = new PrintWriter(sw);
  try {
      hf.printHelp(pw, 10, "usage", "header", opts, 1, 3, "footer", false);
      fail("RuntimeException expected");
  } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("CLI-162"));
  }
  }
  public void testRenderOptionsNormal() {
  TestableHelpFormatter hf = new TestableHelpFormatter();
  Options opts = new Options();
  Option a = new Option("a", "description");
  Option b = new Option("b", "another description");
  opts.addOption(a);
  opts.addOption(b);
  StringBuffer sb = new StringBuffer();
  hf.renderOptionsWrapper(sb, 80, opts, 1, 3);
  String result = sb.toString();
  assertTrue(result.contains("-a"));
  assertTrue(result.contains("-b"));
  assertFalse(result.contains("CLI-162"));
  }
  // --- Miscellaneous -----------------------------------------------------
  public void testSetWidth() {
  HelpFormatter hf = new HelpFormatter();
  hf.setWidth(42);
  assertEquals(42, hf.getWidth());
  }
  public void testNegativeWidth() {
  TestableHelpFormatter hf = new TestableHelpFormatter();
  try {
      hf.findWrapPosWrapper("word", -5, 0);
      fail("RuntimeException expected");
  } catch (RuntimeException e) {
      // expected
  }
  }

}
