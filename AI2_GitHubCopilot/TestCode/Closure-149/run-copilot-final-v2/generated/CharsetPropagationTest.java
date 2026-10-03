package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 import java.io.IOException;
 import java.io.PrintStream;

 /**
  * Tests that the --charset flag is properly propagated from
  * AbstractCommandLineRunner.setRunOptions to CompilerOptions.
  *
  * The reported bug (issue 205) was that {@code getOutputCharset()} returned
  * {@code null} after specifying {@code --charset US-ASCII} because the
  * setter was never called.
  */
 public class CharsetPropagationTest extends TestCase {

     /**
      * Helper that exposes setRunOptions after flag processing so we can
      * intercept the CompilerOptions without actually running the compiler.
      */
     private static class TestRunner extends CommandLineRunner {
         TestRunner(String[] args) {
             super(args, new PrintStream(System.out), new PrintStream(System.err));
         }

         CompilerOptions getOptionsAfterSetRunOptions()
                 throws AbstractCommandLineRunner.FlagUsageException, IOException {
             CompilerOptions options = createOptions();
             setRunOptions(options);
             return options;
         }
     }

     // ---------------------------------------------------------------
     // Positive propagation cases
     // ---------------------------------------------------------------

     public void testCharsetPropagationUSASCII() throws Exception {
         TestRunner runner = new TestRunner(new String[] {"--charset", "US-ASCII"});
         CompilerOptions options = runner.getOptionsAfterSetRunOptions();
         assertNotNull("Output charset should not be null", options.getOutputCharset());
         assertEquals("US-ASCII", options.getOutputCharset());
     }

     public void testCharsetPropagationUTF8() throws Exception {
         TestRunner runner = new TestRunner(new String[] {"--charset", "UTF-8"});
         CompilerOptions options = runner.getOptionsAfterSetRunOptions();
         assertEquals("UTF-8", options.getOutputCharset());
     }

     public void testCharsetPropagationISO8859_1() throws Exception {
         TestRunner runner = new TestRunner(new String[] {"--charset", "ISO-8859-1"});
         CompilerOptions options = runner.getOptionsAfterSetRunOptions();
         assertEquals("ISO-8859-1", options.getOutputCharset());
     }

     // ---------------------------------------------------------------
     // Default / missing flag
     // ---------------------------------------------------------------

     public void testNoCharsetFlagYieldsNull() throws Exception {
         TestRunner runner = new TestRunner(new String[0]);
         CompilerOptions options = runner.getOptionsAfterSetRunOptions();
         assertNull("Output charset must be null when no --charset flag is given",
                 options.getOutputCharset());
     }

     // ---------------------------------------------------------------
     // Boundary / invalid input cases
     // ---------------------------------------------------------------

     public void testCharsetPropagationInvalidCharset() throws Exception {
         TestRunner runner = new TestRunner(new String[] {"--charset", "INVALID-CHARSET"});
         CompilerOptions options = runner.getOptionsAfterSetRunOptions();
         // The exact value must be passed through; validation happens later.
         assertEquals("INVALID-CHARSET", options.getOutputCharset());
     }

     public void testCharsetPropagationEmptyString() throws Exception {
         TestRunner runner = new TestRunner(new String[] {"--charset", ""});
         CompilerOptions options = runner.getOptionsAfterSetRunOptions();
         assertEquals("", options.getOutputCharset());
     }

     // ---------------------------------------------------------------
     // Interaction with other flags
     // ---------------------------------------------------------------

     public void testCharsetPropagationWithOtherFlags() throws Exception {
         TestRunner runner = new TestRunner(new String[] {
                 "--charset", "US-ASCII",
                 "--debug"
         });
         CompilerOptions options = runner.getOptionsAfterSetRunOptions();
         assertEquals("US-ASCII", options.getOutputCharset());
     }

     // ---------------------------------------------------------------
     // Ensure the right method is responsible
     // ---------------------------------------------------------------

     public void testCreateOptionsAloneDoesNotSetCharset() throws Exception {
         TestRunner runner = new TestRunner(new String[] {"--charset", "US-ASCII"});
         // Call createOptions without the subsequent setRunOptions.
         CompilerOptions options = runner.createOptions();
         assertNull("createOptions() must not set output charset (this is setRunOptions job)",
                 options.getOutputCharset());
     }
 }
