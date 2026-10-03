package org.apache.commons.cli.bug24;

 import java.io.*;

 import junit.framework.*;

 import org.apache.commons.cli.*;

 public class HelpFormatterBugTest extends TestCase {

     private HelpFormatter formatter;

     protected void setUp() {
         formatter = new HelpFormatter();
         formatter.setNewLine("\n"); // deterministic across platforms
     }

     // Helper: render options to a String without touching stdout
     private String renderOptionsToString(Options options, int width, int leftPad, int descPad) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         formatter.printOptions(pw, width, options, leftPad, descPad);
         pw.flush();
         return sw.toString();
     }

     // Helper: create an Option concisely
     private Option createOption(String shortOpt, String longOpt, boolean hasArg, String desc) {
         return new Option(shortOpt, longOpt, hasArg, desc);
     }

     // 1. Normal: short description fits entirely on the first line
     public void testShortDescriptionNoWrap() {
         Options opts = new Options();
         opts.addOption(createOption("a", "alpha", false, "short"));

         String output = renderOptionsToString(opts, 80, 1, 3);

         assertTrue("missing option", output.indexOf("-a") != -1);
         assertTrue("missing description", output.indexOf("short") != -1);
         // no newline after the description part
         int descIdx = output.indexOf("short");
         assertEquals("description should not be wrapped", -1, output.indexOf('\n', descIdx));
     }

     // 2. Boundary: description fills remaining width exactly – no wrap
     public void testExactFitNoWrap() {
         // width=20, leftPad=1, header "-x,--xray" = 12 chars, descPad=3 => nextLineTabStop=15
         // remaining space for desc = 20-15 = 5 chars
         Options opts = new Options();
         opts.addOption(createOption("x", "xray", false, "abcde"));

         String output = renderOptionsToString(opts, 20, 1, 3);

         assertTrue("description not found", output.indexOf("abcde") != -1);
         int start = output.indexOf("-x");
         if (start == -1) start = output.indexOf("--xray");
         int endOfLine = output.indexOf('\n', start);
         assertEquals("exact fit should not wrap", -1, endOfLine);
     }

     // 3. Long description wraps with correct indentation (nextLineTabStop < width)
     public void testLongDescriptionWrapsWithIndent() {
         // width=40, leftPad=1, header "-f,--file <arg>" length ~17, max=17, descPad=3 =>
nextLineTabStop=20
         Options opts = new Options();
         opts.addOption(createOption("f", "file", true, "specify a file name for the output, must
exist"));

         String output = renderOptionsToString(opts, 40, 1, 3);

         int firstNewline = output.indexOf('\n');
         assertTrue("expected continuation line", firstNewline != -1);
         String continuation = output.substring(firstNewline + 1);
         // continuation should be padded with 20 spaces
         String expectedIndent = "                    "; // 20 spaces
         assertTrue("missing indent", continuation.startsWith(expectedIndent));
     }

     // 4. Edge: indent equals width (buggy version throws, fix should not)
     public void testWidthEqualsIndentShouldNotThrow() {
         // width=17, header "-t,--timeout" = 13 + leftPad 1 = 14, max=14, descPad=3 =>
nextLineTabStop=17
         Options opts = new Options();
         opts.addOption(createOption("t", "timeout", false, "sets the timeout value in seconds for
the connection"));

         String output = null;
         try {
             output = renderOptionsToString(opts, 17, 1, 3);
         } catch (IllegalStateException e) {
             fail("should not throw IllegalStateException when indent == width");
         }
         assertTrue("output must not be empty", output != null && output.length() > 0);
     }

     // 5. Edge: indent > width (buggy version throws, fix should not)
     public void testIndentGreaterThanWidthShouldNotThrow() {
         // header "-v,--verbose-output" length 21 + leftPad 1 = 22, max=22, descPad=3 =>
nextLineTabStop=25
         // width=20 < 25
         Options opts = new Options();
         opts.addOption(createOption("v", "verbose-output", false, "enable extra logging for
debugging"));

         try {
             String output = renderOptionsToString(opts, 20, 1, 3);
             assertTrue("should produce some text", output.length() > 0);
         } catch (IllegalStateException e) {
             fail("IllegalStateException should not be thrown");
         }
     }

     // 6. Very large left padding with narrow width – description short enough to avoid wrap
     public void testLargeLeftPaddingNarrowWidthShortDesc() {
         // leftPad=10, width=20, descPad=3
         Options opts = new Options();
         opts.addOption(createOption("q", "quiet", false, "run"));

         try {
             String output = renderOptionsToString(opts, 20, 10, 3);
             assertTrue("option should be present", output.indexOf("-q") != -1);
         } catch (IllegalStateException e) {
             fail("should handle large leftPad with narrow width without exception");
         }
     }

     // 7. Zero padding – no indent at all
     public void testZeroPadding() {
         Options opts = new Options();
         opts.addOption(createOption("r", "recurse", false, "recurse into subdirectories to great
depth"));

         String output = renderOptionsToString(opts, 40, 0, 0);

         // option line must start without spaces
         assertTrue("unexpected left padding", output.startsWith("-r"));

         int nl = output.indexOf('\n');
         if (nl != -1) {
             String cont = output.substring(nl + 1);
             // no indentations on continuation lines
             assertTrue("continuation must not be indented", cont.charAt(0) != ' ');
         }
     }

     // 8. Multiple options – uniform indent computed from longest option
     public void testMultipleOptionsUniformIndent() {
         Options opts = new Options();
         opts.addOption(createOption("a", null, false, "alpha"));
         opts.addOption(createOption("b", "beta", true, "beta description that wraps"));

         String output = renderOptionsToString(opts, 30, 2, 4);

         assertTrue("missing -a", output.indexOf("-a") != -1);
         assertTrue("missing -b", output.indexOf("-b") != -1);

         // The longest header determines nextLineTabStop; both options should use the same
indentation
         // Simple check: all continuation lines (those after the first newline) start with same
number of spaces
         String[] lines = output.split("\n", -1);
         int continuationSpaces = -1;
         for (int i = 1; i < lines.length; i++) {
             if (lines[i].startsWith(" ") && lines[i].trim().length() > 0) {
                 int spaces = lines[i].length() - lines[i].trim().length();
                 if (continuationSpaces == -1) {
                     continuationSpaces = spaces;
                 } else {
                     assertEquals("inconsistent continuation indent", continuationSpaces, spaces);
                 }
             }
         }
     }

     // 9. Description containing explicit newline – break early and indent continuation
     public void testDescriptionWithNewlineAndIndent() {
         Options opts = new Options();
         opts.addOption(createOption("n", "newline-test", false, "line1\nline2 continuation"));

         String output = renderOptionsToString(opts, 80, 1, 3);

         assertTrue("line2 missing", output.indexOf("line2") != -1);
         // The second line should be indented (nextLineTabStop of about 15+3=18 spaces)
         int line2Idx = output.indexOf("line2");
         int lineStart = output.lastIndexOf('\n', line2Idx) + 1;
         String line2 = output.substring(lineStart, line2Idx);
         assertTrue("line2 must be indented", line2.startsWith("                  ")); // at least
some spaces
     }

     // 10. Very long option prefix, no description – should not trigger wrapping logic
     public void testLongOptionNoDescription() {
         Options opts = new Options();
         opts.addOption(createOption("L", "this-is-a-very-long-option-name-that-eats-width", false,
null));

         try {
             String output = renderOptionsToString(opts, 30, 1, 3);
             assertTrue("option not present", output.indexOf("this-is-a-very-long") != -1);
         } catch (IllegalStateException e) {
             fail("should not throw when description is null, even with narrow width");
         }
     }

     // 11. Boundary: nextLineTabStop just under width (width = indent + 1)
     public void testIndentJustUnderWidthLongWrap() {
         // header "-p,--p" length 9, leftPad=1 => max=9, descPad=3 => nextLineTabStop=12, width=13
         Options opts = new Options();
         opts.addOption(createOption("p", "p", false, "very long description that certainly wraps
over multiple lines"));

         try {
             String output = renderOptionsToString(opts, 13, 1, 3);
             assertTrue("description fragment missing", output.indexOf("very long") != -1);
         } catch (IllegalStateException e) {
             fail("should not throw when width = nextLineTabStop + 1");
         }
     }

     // 12. Default settings with a long description (triggers the real CLI-162 scenario)
     public void testDefaultSettingsLongDescriptionNoThrow() {
         formatter = new HelpFormatter();
         formatter.setNewLine("\n");
         Options opts = new Options();
         opts.addOption(createOption("c", "config", true,
                 "Specify configuration file path. The path must be absolute or relative and only
one file is allowed."));

         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         formatter.printOptions(pw, 74, opts, 1, 3);
         pw.flush();
         String output = sw.toString();

         assertTrue("option -c must appear", output.indexOf("-c") != -1);
         assertTrue("description must appear", output.indexOf("Specify configuration file") != -1);
         // No IllegalStateException should have been thrown (this test fails on the buggy version)
     }

 }