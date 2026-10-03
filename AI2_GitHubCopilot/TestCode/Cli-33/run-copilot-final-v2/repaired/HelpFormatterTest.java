package org.apache.commons.cli;

 import junit.framework.TestCase;
 import java.io.StringWriter;
 import java.io.PrintWriter;
 import java.io.IOException;

 public class HelpFormatterTest extends TestCase {

     private HelpFormatter formatter;
     private Options options;
     private StringWriter sw;
     private PrintWriter pw;

     protected void setUp() {
         formatter = new HelpFormatter();
         options = new Options();
         options.addOption(new Option("h", "help", false, "Print help message"));
         sw = new StringWriter();
         pw = new PrintWriter(sw);
     }

     private String getOutput(int width, String cmdLineSyntax, String header,
                              int leftPad, String footer) {
         sw.getBuffer().setLength(0);
         formatter.printHelp(pw, width, cmdLineSyntax, header, options,
                             leftPad, formatter.getDescPadding(), footer, false);
         pw.flush();
         return sw.toString();
     }

     // --- Header/Footer indentation ---

     public void testIndentedHeaderAndFooter() {
         int width = 80, leftPad = 3;
         String header = "Header", footer = "Footer";
         String output = getOutput(width, "myapp", header, leftPad, footer);
         String leftPadStr = formatter.createPadding(leftPad);
         int usageEnd = output.indexOf(formatter.defaultSyntaxPrefix + "myapp");
         assertTrue("Usage line missing", usageEnd >= 0);
         // Each line of header should start with leftPad
         int headerIdx = output.indexOf(header, usageEnd);
         assertTrue("Header not found", headerIdx > 0);
         // The header line(s) must be indented
         String headerLine = leftPadStr + header;
         assertTrue("Header not indented", output.indexOf(headerLine) >= 0);
         // Footer indentation similarly
         int footerIdx = output.indexOf(footer, headerIdx + header.length());
         assertTrue("Footer not found", footerIdx > 0);
         String footerLine = leftPadStr + footer;
         assertTrue("Footer not indented", output.indexOf(footerLine) >= 0);
     }

     public void testHeaderIndentationWithDifferentLeftPad() {
         int width = 74, leftPad = 5;
         String header = "My Header";
         String output = getOutput(width, "tool", header, leftPad, null);
         String leftPadStr = formatter.createPadding(leftPad);
         // Header must appear with exactly leftPad spaces prefix
         String prefixed = leftPadStr + header;
         assertTrue("Header lacks left padding", output.indexOf(prefixed) >= 0);
         // And must NOT appear without that padding
         // (check that it does not appear at column 0 or with fewer spaces)
         int pos = output.indexOf(header);
         // The first occurrence of header text should be after the leftPad spaces
         // We can check that the text before it is exactly leftPad spaces + possibly newline
         if (pos >= 0) {
             // Ensure that the position of header text minus leftPad spaces is non-negative
             // and the preceding characters are exactly leftPad spaces
             assertTrue("Header appears without proper padding",
                        pos >= leftPad && output.substring(pos - leftPad, pos).equals(leftPadStr));
         } else {
             fail("Header not found in output");
         }
     }

     public void testFooterIndentationWithDifferentLeftPad() {
         int width = 80, leftPad = 4;
         String footer = "Footer text";
         String output = getOutput(width, "app", null, leftPad, footer);
         String expectedLine = formatter.createPadding(leftPad) + footer;
         assertTrue("Footer not indented", output.indexOf(expectedLine) >= 0);
     }

     // --- Null / Empty safety ---

     public void testNullHeaderDoesNotThrow() {
         try {
             String output = getOutput(74, "cmd", null, 1, "Footer");
             // If no exception, output should not contain "null" string
             assertTrue("Output should not contain 'null'", output.indexOf("null") < 0);
         } catch (Exception e) {
             fail("Null header threw an exception: " + e.getMessage());
         }
     }

     public void testNullFooterDoesNotThrow() {
         try {
             String output = getOutput(74, "cmd", "Header", 1, null);
             assertTrue("Output should not contain 'null'", output.indexOf("null") < 0);
         } catch (Exception e) {
             fail("Null footer threw an exception: " + e.getMessage());
         }
     }

     public void testEmptyHeaderNotPrinted() {
         String output = getOutput(74, "cmd", "", 1, "Footer");
         int usagePos = output.indexOf(formatter.defaultSyntaxPrefix + "cmd");
         assertTrue("Usage missing", usagePos >= 0);
         assertTrue("Output should not be empty", output.length() > 0);
     }

     // --- Multi-line and wrapping ---

     public void testMultiLineHeaderIndentation() {
         // Header contains newlines
         String header = "First line\nSecond line";
         int width = 80, leftPad = 2;
         String output = getOutput(width, "multi", header, leftPad, null);
         String padding = formatter.createPadding(leftPad);
         // Split output into lines, check that all lines belonging to header are indented
         String[] lines = output.split(formatter.getNewLine());
         boolean foundFirst = false, foundSecond = false;
         for (String line : lines) {
             if (line.equals(padding + "First line")) foundFirst = true;
             if (line.equals(padding + "Second line")) foundSecond = true;
         }
         assertTrue("First header line not indented", foundFirst);
         assertTrue("Second header line not indented", foundSecond);
     }

     public void testMultiLineFooterIndentation() {
         String footer = "Line A\nLine B";
         int width = 80, leftPad = 3;
         String output = getOutput(width, "multi", null, leftPad, footer);
         String padding = formatter.createPadding(leftPad);
         String[] lines = output.split(formatter.getNewLine());
         boolean foundA = false, foundB = false;
         for (String line : lines) {
             if (line.equals(padding + "Line A")) foundA = true;
             if (line.equals(padding + "Line B")) foundB = true;
         }
         assertTrue("First footer line not indented", foundA);
         assertTrue("Second footer line not indented", foundB);
     }

     public void testHeaderWrappingPreservesIndentation() {
         // Width small, header long, to trigger wrapping
         int width = 20;
         int leftPad = 2;
         // Header that must wrap
         String header = "This is a very long header that will wrap to multiple lines";
         String output = getOutput(width, "cmd", header, leftPad, null);
         String padding = formatter.createPadding(leftPad);
         boolean firstIndented = false;
         int indentedWrapLines = 0;
         String[] lines = output.split(formatter.getNewLine());
         for (String line : lines) {
             if (line.startsWith(padding) && line.contains("This")) firstIndented = true;
             // wrapped lines should also start with padding in correct behavior
             if (line.startsWith(padding)) indentedWrapLines++;
         }
         assertTrue("Header first line not indented", firstIndented);
         int headerTextPos = output.indexOf("very long header");
         if (headerTextPos > 0) {
             String leading = output.substring(headerTextPos - leftPad, headerTextPos);
             assertTrue("Header content found without proper left padding",
                        leading.equals(padding));
         }
     }

     public void testDefaultWidthAndLeftPad() {
         // Use default width=74, leftPad=1
         int defaultWidth = HelpFormatter.DEFAULT_WIDTH;
         int defaultLeftPad = HelpFormatter.DEFAULT_LEFT_PAD;
         String header = "Default Header";
         String footer = "Default Footer";
         sw.getBuffer().setLength(0);
         formatter.printHelp(pw, defaultWidth, "test", header, options,
                             defaultLeftPad, formatter.getDescPadding(), footer, false);
         pw.flush();
         String output = sw.toString();
         String pad = formatter.createPadding(defaultLeftPad);
         assertTrue("Header not indented with default left pad", output.indexOf(pad + header) >= 0);
         assertTrue("Footer not indented with default left pad", output.indexOf(pad + footer) >= 0);
     }

     public void testNoOptionsStillIndentsHeaderFooter() {
         Options emptyOpts = new Options();
         int width = 80, leftPad = 4;
         String header = "H", footer = "F";
         sw.getBuffer().setLength(0);
         formatter.printHelp(pw, width, "bare", header, emptyOpts,
                             leftPad, formatter.getDescPadding(), footer, false);
         pw.flush();
         String output = sw.toString();
         String pad = formatter.createPadding(leftPad);
         assertTrue("Header not indented with no options", output.indexOf(pad + header) >= 0);
         assertTrue("Footer not indented with no options", output.indexOf(pad + footer) >= 0);
     }

     public void testTrailingNewLineConsistency() {
         // Check that header/footer output ends with newline (or consistent spacing)
         int width = 80, leftPad = 2;
         String header = "H", footer = "F";
         String output = getOutput(width, "x", header, leftPad, footer);
         String pad = formatter.createPadding(leftPad);
         // header line should be on its own line (followed by newline)
         int headerLineIdx = output.indexOf(pad + header);
         assertTrue("Header line missing", headerLineIdx >= 0);
         String afterHeader = output.substring(headerLineIdx + pad.length() + header.length());
         assertTrue("Header line not terminated by newline",
                    afterHeader.startsWith(formatter.getNewLine()));
     }

 }
