package org.apache.commons.cli.bug;

 import java.io.PrintWriter;
 import java.io.StringWriter;

 import junit.framework.TestCase;

 import org.apache.commons.cli.HelpFormatter;
 import org.apache.commons.cli.Option;
 import org.apache.commons.cli.Options;
 import org.apache.commons.cli.OptionGroup;

 public class HelpFormatterCLI162Test extends TestCase {

     private HelpFormatter formatter;
     private StringWriter sw;
     private PrintWriter pw;

     protected void setUp() {
         formatter = new HelpFormatter();
         formatter.setNewLine("\n");
         sw = new StringWriter();
         pw = new PrintWriter(sw);
     }

     protected void tearDown() {
         pw.close();
     }

     private String renderOptionsToString(int width, Options options, int leftPad, int descPad) {
         formatter.printOptions(pw, width, options, leftPad, descPad);
         pw.flush();
         String result = sw.toString();
         sw.getBuffer().setLength(0);
         return result;
     }

     private String padding(int len) {
         StringBuffer sb = new StringBuffer(len);
         for (int i = 0; i < len; i++) {
             sb.append(' ');
         }
         return sb.toString();
     }

     /** Exposes the protected findWrapPos for direct testing. */
     static class TestableHelpFormatter extends HelpFormatter {
         public int wrapPos(String text, int width, int startPos) {
             return findWrapPos(text, width, startPos);
         }
     }

     // ---------- bug-related tests ----------

     public void testLongDescriptionContinuationLineIndented() {
         Options opts = new Options();
         Option opt = new Option("a", "long-option", false,
                 "This is a very long description that will wrap onto multiple lines.");
         opts.addOption(opt);

         int width = 30;
         int leftPad = 1;
         int descPad = 3;
         String output = renderOptionsToString(width, opts, leftPad, descPad);

         // expected nextLineTabStop = max prefix length (18) + descPad (3) = 21
         int nextLineTabStop = 21;
         String indent = padding(nextLineTabStop);

         int firstNewline = output.indexOf('\n');
         assertTrue("Output must contain a newline", firstNewline > 0);

         String secondLineStart = output.substring(firstNewline + 1,
                 Math.min(firstNewline + 1 + nextLineTabStop, output.length()));
         assertEquals("Continuation line must be indented by " + nextLineTabStop + " spaces",
                 indent, secondLineStart);

         assertTrue("Second line must have content after indent",
                 output.length() > firstNewline + 1 + nextLineTabStop);
     }

     public void testMultipleOptionsContinuationLinesAlign() {
         Options opts = new Options();
         Option opt1 = new Option("a", "alpha", false,
                 "First very long description that definitely needs wrapping across several
lines.");
         Option opt2 = new Option("b", "beta", true,
                 "Second lengthy description spreading over multiple text lines.");
         opts.addOption(opt1);
         opts.addOption(opt2);

         int width = 45;
         String output = renderOptionsToString(width, opts, 1, 3);
         String[] lines = output.split("\n");

         // After the first option's initial line, all continuation lines must start with the
         // same indent (maxPrefixLength + descPad). The bug caused some lines to miss the indent.
         int continuationIndent = -1;
         for (int i = 1; i < lines.length; i++) {
             String line = lines[i];
             // rough detection of a continuation line: starts with spaces
             if (line.length() > 0 && line.charAt(0) == ' ') {
                 int spaces = 0;
                 while (spaces < line.length() && line.charAt(spaces) == ' ') {
                     spaces++;
                 }
                 if (continuationIndent == -1) {
                     continuationIndent = spaces;
                 }
                 assertEquals("All continuation lines must have the same indent",
                         continuationIndent, spaces);
                 assertTrue("Indent must be at least leftPad+descPad (4)",
                         continuationIndent >= 4);
             }
         }
         assertTrue("Must have at least one continuation line", continuationIndent > 0);
     }

     public void testOptionGroupLongDescriptionIndented() {
         OptionGroup group = new OptionGroup();
         group.addOption(new Option("x", "extract", false,
                 "Extract files with a very long description that must wrap and keep
indentation."));
         group.addOption(new Option("c", "create", false, "Short desc"));
         Options opts = new Options();
         opts.addOptionGroup(group);

         int width = 35;
         String output = renderOptionsToString(width, opts, 1, 3);

         assertTrue("Should contain wrapped text", output.indexOf('\n') >= 0);
         // simple sanity: after newline, the next line should not be empty
         int nl = output.indexOf('\n');
         assertTrue("Line after newline must not be empty",
                 nl + 1 < output.length() && output.charAt(nl + 1) != '\n');
     }

     // ---------- findWrapPos tests ----------

     public void testFindWrapPosNoWrapNeeded() {
         TestableHelpFormatter hf = new TestableHelpFormatter();
         // text fits within width
         assertEquals(-1, hf.wrapPos("Hello world", 50, 0));
     }

     public void testFindWrapPosBreakAtSpace() {
         TestableHelpFormatter hf = new TestableHelpFormatter();
         // width 10: "This is a long..."  - last space before index 10 is at index 9
         assertEquals(9, hf.wrapPos("This is a long string", 10, 0));
     }

     public void testFindWrapPosNoSpaceBeforeWidth() {
         TestableHelpFormatter hf = new TestableHelpFormatter();
         // no space available within width -> returns -1 (or width? – assert -1 for buggy
         // version's intended behaviour, even if the follow-up logic may break)
         assertEquals(-1, hf.wrapPos("Supercalifragilisticexpialidocious", 10, 0));
     }

     // ---------- normal / boundary behaviour ----------

     public void testDescriptionExactlyAtWidthNoWrap() {
         Options opts = new Options();
         Option opt = new Option("a", false, "abcd");
         opts.addOption(opt);

         // width such that prefix + descPad + description exactly fits
         int width = 10; // prefix " -a"=3, descPad=3, desc="abcd"=4 => total 10
         String output = renderOptionsToString(width, opts, 1, 3);
         assertFalse("Description that fits should not wrap", output.indexOf('\n') >= 0);
     }

     public void testNullDescriptionSafe() {
         Options opts = new Options();
         Option opt = new Option("n", "null-desc", false, (String) null);
         opts.addOption(opt);

         String output = renderOptionsToString(74, opts, 1, 3);
         assertTrue("Option should be present", output.indexOf("--null-desc") >= 0);
         // no crash is the primary assertion
     }

     public void testEmptyDescription() {
         Options opts = new Options();
         Option opt = new Option("e", "empty-desc", false, "");
         opts.addOption(opt);

         String output = renderOptionsToString(74, opts, 1, 3);
         assertTrue("Option should appear", output.indexOf("--empty-desc") >= 0);
     }

     public void testPrintWrappedWithIndent() {
         StringWriter sw2 = new StringWriter();
         PrintWriter pw2 = new PrintWriter(sw2);
         formatter.printWrapped(pw2, 20, 5, "This is a long text that should wrap with indent.");
         pw2.flush();
         String output = sw2.toString();
         pw2.close();

         String[] lines = output.split("\n");
         assertTrue("Should wrap into multiple lines", lines.length > 1);
         assertEquals("This is a long text", lines[0].trim());
         assertTrue("Continuation line must be indented by 5 spaces",
                 lines[1].startsWith("     "));
     }

     public void testExtremelyLongArgNameWrapsCorrectly() {
         Options opts = new Options();
         Option opt = new Option("l", "loooooooooooooooooooooong-option", true,
                 "A really long description here wrapping many times over multiple lines.");
         opts.addOption(opt);

         int width = 40;
         String output = renderOptionsToString(width, opts, 1, 3);
         String[] lines = output.split("\n");

         for (int i = 1; i < lines.length; i++) {
             String line = lines[i];
             if (line.length() > 0 && Character.isLetter(line.charAt(0))) {
                 fail("Continuation line must start with indent, but got: " + line);
             }
         }
     }
 }