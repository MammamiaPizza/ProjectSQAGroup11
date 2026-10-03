package org.apache.commons.cli.bug24;

 import java.io.*;
 import java.util.*;

 import junit.framework.*;

 import org.apache.commons.cli.*;

 public class HelpFormatterBugTest extends TestCase {

     private HelpFormatter formatter;

     protected void setUp() {
         formatter = new HelpFormatter();
         formatter.setNewLine("\n"); // deterministic line separations
     }

     // Helper: render options to a String
     private String renderOptionsToString(Options options, int width, int leftPad, int descPad) {
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         formatter.printOptions(pw, width, options, leftPad, descPad);
         pw.flush();
         return sw.toString();
     }

     // Helper: create an Option easily
     private Option createOption(String shortOpt, String longOpt, boolean hasArg, String
description) {
         return new Option(shortOpt, longOpt, hasArg, description);
     }

     // 1. Normal: short description fits entirely on the first line
     public void testShortDescriptionNoWrap() {
         Options opts = new Options();
         opts.addOption(createOption("a", "alpha", false, "short"));

         String output = renderOptionsToString(opts, 80, 1, 3);

         assertTrue("should contain option", output.indexOf("-a") != -1);
         assertTrue("should contain description", output.indexOf("short") != -1);
         // No newline inside the description part
         int descIdx = output.indexOf("short");
         assertTrue("no newline after desc", output.indexOf('\n', descIdx) == -1);
     }

     // 2. Boundary: description length exactly fills remaining width – no wrap
     public void testExactFitNoWrap() {
         // leftPad=1, opt "-x" (2), longOpt "--xray" makes header: "1 space + -x,--xray" => 12
chars
         // max will be ~12, nextLineTabStop = 12+descPad=15, width=20 => description space=5 chars
         Options opts = new Options();
         opts.addOption(createOption("x", "xray", false, "abcde"));

         String output = renderOptionsToString(opts, 20, 1, 3);

         assertTrue(output.indexOf("abcde") != -1);
         // entire option should be on a single line
         int start = output.indexOf("-x");
         if (start == -1) start = output.indexOf("--xray");
         int end = output.indexOf('\n', start);
         assertTrue("no wrap when exact fit", end == -1);
     }

     // 3. Long description wraps with proper indentation (nextLineTabStop < width)
     public void testLongDescriptionWrapsWithIndent() {
         Options opts = new Options();
         opts.addOption(createOption("f", "file", true, "specify a file name for the output, must
exist"));

         // width=40, leftPad=1, opt header "-f,--file <arg>" length: " -f,--file <arg>" = 17
(including leading space)
         // max=17, nextLineTabStop=20 (17+3), width=40 -> wrap should happen
         String output = renderOptionsToString(opts, 40, 1, 3);

         // After first line there should be continuation line(s) with indent of 20 spaces
         int firstNewline = output.indexOf('\n');
         assertTrue("should have continuation", firstNewline != -1);
         String continuation = output.substring(firstNewline + 1);
         // continuation should start with 20 spaces (or more if repeated padding)
         assertTrue("indent applied", continuation.startsWith("                    ")); // 20 spaces
     }

     // 4. Narrow width when indent equals width (buggy throws, fixed should not)
     public void testWidthEqualIndentShouldNotThrow() {
         Options opts = new Options();
         // Create an option whose header length + descPad == width
         // header: "-t,--timeout" = 13 chars + leftPad 1 = 14, max=14, descPad=3 =>
nextLineTabStop=17
         // width=17
         opts.addOption(createOption("t", "timeout", false, "sets the timeout value in seconds for
the connection"));

         String output = null;
         try {
             output = renderOptionsToString(opts, 17, 1, 3);
         } catch (IllegalStateException e) {
             // Bug present – will be marked as failure
             fail("should not throw IllegalStateException when indent == width");
         }
         assertTrue("output should not be empty", output != null && output.length() > 0);
         // Even if it does not throw, the description may be unreadable but no exception
     }

     // 5. Extreme: indent > width (will throw in buggy version)
     public void testIndentGreaterThanWidthShouldNotThrow() {
         Options opts = new Options();
         // header "-v,--verbose-output" = 21 + leftPad 1 = 22, max=22, descPad=3 =>
nextLineTabStop=25
         // width=20 < 25
         opts.addOption(createOption("v", "verbose-output", false, "enable extra logging for
debugging purposes"));

         try {
             String output = renderOptionsToString(opts, 20, 1, 3);
             // If it reaches here, the bug might be fixed; just ensure output is not empty
             assertTrue(output.length() > 0);
         } catch (IllegalStateException e) {
             fail("IllegalStateException should not be thrown");
         }
     }

     // 6. Very large left padding, small width – indent near zero room
     public void testLargeLeftPaddingSmallWidth() {
         Options opts = new Options();
         opts.addOption(createOption("q", "quiet", false, "run quietly"));
         // leftPad=10, width=20, descPad=3
         // header: 10 spaces + "-q,--quiet" (9) = 19, max=19, nextLineTabStop=22 > width=20, but
description short maybe no wrap
         // Actually the text will have header 19 + descPad=22 + description "run quietly" -> total
> width, findWrapPos may return -1? Let's test whether it throws.
         try {
             String output = renderOptionsToString(opts, 20, 10, 3);
             // In buggy version may throw because nextLineTabStop >= width; fixed version should
not
             assertTrue("output should contain option", output.indexOf("-q") != -1);
         } catch (IllegalStateException e) {
             fail("should handle large leftPad with narrow width without exception");
         }
     }

     // 7. Zero padding – indent should be minimal
     public void testZeroPadding() {
         Options opts = new Options();
         opts.addOption(createOption("r", "recurse", false, "recurse into subdirectories"));
         // leftPad=0, descPad=0, width=40
         String output = renderOptionsToString(opts, 40, 0, 0);
         // The output should start with no leading spaces; the option line should be flush
         assertTrue("no left pad", output.startsWith("-r"));
         // If wrap occurs (description long enough), continuation should have no indent
         int nl = output.indexOf('\n');
         if (nl != -1) {
             String cont = output.substring(nl + 1);
             assertTrue("continuation no left pad", !cont.startsWith(" "));
         }
     }

     // 8. Multiple options – ensure max is computed across all, and each option uses same
nextLineTabStop
     public void testMultipleOptionsUniformIndent() {
         Options opts = new Options();
         opts.addOption(createOption("a", null, false, "alpha description"));
         opts.addOption(createOption("b", "beta", true, "beta description long enough to wrap
maybe"));

         String output = renderOptionsToString(opts, 40, 2, 4);
         // max should be determined by the longest header "b,--beta <arg>" etc
         // Check that both options exist
         assertTrue(output.indexOf("-a") != -1);
         assertTrue(output.indexOf("-b") != -1);
         // both options' continuation lines (if any) should start with the same indent
         // simple check: all lines after first for each option start with same number of spaces
     }

     // 9. Description that contains explicit newline – should break early and indent correctly
     public void testDescriptionWithNewlineAndIndent() {
         Options opts = new Options();
         opts.addOption(createOption("n", "newline-test", false, "line1\nline2 continuation"));
         String output = renderOptionsToString(opts, 80, 1, 3);
         // The description should be split at '\n', and the second line should be indented
         assertTrue("contains line2", output.indexOf("line2") != -1);
         // Find the line containing "line2" and check its leading spaces
         // We'll not implement complex check here, just assert it does not throw
     }

     // 10. Very long option prefix (no description) – still should not throw
     public void testLongOptionNoDescription() {
         Options opts = new Options();
         opts.addOption(createOption("L",
"this-is-a-very-long-option-name-that-makes-header-eat-width", false, null));
         // header will be massive; nextLineTabStop will be huge; but description is null so
renderWrappedText may not be called? Actually renderOptions checks if description != null. If null,
it won't append description, so the text is just the header, no wrap needed. Should not throw.
         try {
             String output = renderOptionsToString(opts, 30, 1, 3);
             assertTrue("output contains option", output.indexOf("this-is-a-very-long") != -1);
         } catch (Exception e) {
             fail("should not throw when description is null, even with narrow width");
         }
     }

     // 11. Boundary: nextLineTabStop just under width, long description wrapping
     public void testIndentJustUnderWidthLongWrap() {
         Options opts = new Options();
         // leftPad=1, opt "p", longOpt "p" => header " -p,--p" = 9, max=9, descPad=3 => 12,
width=13, nextLineTabStop=12
         // (width > nextLineTabStop by 1)
         opts.addOption(createOption("p", "p", false, "very long description that certainly wraps
over multiple lines"));
         try {
             String output = renderOptionsToString(opts, 13, 1, 3);
             assertTrue("description present", output.indexOf("very long") != -1);
         } catch (IllegalStateException e) {
             fail("should not throw when width = nextLineTabStop + 1");
         }
     }

     // 12. Default formatter settings with typical width and long description
     public void testDefaultSettingsWithLongDescription() {
         // Use default width=74, leftPad=1, descPad=3
         formatter = new HelpFormatter(); // fresh with defaults (newLine already set)
         Options opts = new Options();
         opts.addOption(createOption("c", "config", true, "Specify configuration file path. The path
must be absolute or relative to the current working directory. Multiple files are not allowed."));
         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);
         // Use printOptions directly
         formatter.printOptions(pw, 74, opts, 1, 3);
         pw.flush();
         String output = sw.toString();
         assertTrue("should start with -c", output.indexOf("-c") != -1);
         assertTrue("description chunked", output.indexOf("Specify configuration file") != -1);
         // Should not throw
     }
 }

Note: I used mistypes like "IllegalStateException" but correct is "IllegalStateException" (capital
S). I'll correct to "IllegalStateException" (the original bug throws
java.lang.IllegalStateException). The code above has typos: "IllegalStateException", "fail",
"assertTrue", etc. I also used "stsWith" instead of "startsWith". I need to produce correct Java
code.

I'll revise to fully correct Java code with correct class names and method names. JUnit 3.8.1 has
assertTrue(boolean), assertFalse(boolean), assertEquals, fail(String). So use those.

I'll produce final answer.```java
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
     opts.addOption(createOption("t", "timeout", false, "sets the timeout value in seconds for the
connection"));

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

     // The longest header determines nextLineTabStop; both options should use the same indentation
     // Simple check: all continuation lines (those after the first newline) start with same number
of spaces
     String[] lines = output.split("\n", -1);
     int continuationSpaces = -1;
     for (int i = 1; i < lines.length; i++) { // skip first line (which is the whole output?
Actually first line is first option's first line)
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
     assertTrue("line2 must be indented", line2.startsWith("                  ")); // at least some
spaces
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
     opts.addOption(createOption("p", "p", false, "very long description that certainly wraps over
multiple lines"));

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
             "Specify configuration file path. The path must be absolute or relative and only one
file is allowed."));

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
```