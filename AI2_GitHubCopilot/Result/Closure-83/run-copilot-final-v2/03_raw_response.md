package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 import java.io.ByteArrayOutputStream;
 import java.io.PrintStream;

 /**
  * Tests for {@link CommandLineRunner} behavior when the --version flag is
  * provided.  These tests encode the contract that --version alone causes
  * the version string to be printed to stdout, causes stderr to remain
  * empty, does not start the compiler, and takes precedence over other
  * flags (including --help and invalid flags).
  */
 public class CommandLineRunnerVersionTest extends TestCase {

     private ByteArrayOutputStream outContent;
     private ByteArrayOutputStream errContent;
     private PrintStream out;
     private PrintStream err;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         outContent = new ByteArrayOutputStream();
         errContent = new ByteArrayOutputStream();
         out = new PrintStream(outContent);
         err = new PrintStream(errContent);
     }

     /**
      * --version alone must cause {@link CommandLineRunner#shouldRunCompiler()}
      * to return false.
      */
     public void testShouldRunCompilerFalseForVersionFlag() {
         CommandLineRunner runner =
             new CommandLineRunner(new String[] {"--version"}, out, err);
         assertFalse("shouldRunCompiler must return false when --version is specified",
                     runner.shouldRunCompiler());
     }

     /**
      * Version output must appear on stdout, not stderr.
      */
     public void testVersionOutputToStdout() {
         new CommandLineRunner(new String[] {"--version"}, out, err);
         String stdout = outContent.toString();
         String stderr = errContent.toString();
         assertTrue("Version string should appear in stdout", stdout.contains("Version:"));
         assertTrue("stdout should start with 'Closure Compiler'",
                     stdout.startsWith("Closure Compiler"));
         assertEquals("stderr should be empty", "", stderr);
     }

     /**
      * Even when --version is combined with other recognised flags, the
      * version information is shown and the compiler is not started.
      */
     public void testVersionFlagWithOtherFlags() {
         CommandLineRunner runner = new CommandLineRunner(
                 new String[] {"--version", "--js", "dummy.js"}, out, err);
         assertFalse(runner.shouldRunCompiler());
         assertTrue(outContent.toString().startsWith("Closure Compiler"));
         assertEquals("stderr must remain empty", "", errContent.toString());
     }

     /**
      * When both --version and --help are given, the version should be
      * printed and no help text emitted.
      */
     public void testVersionFlagWithHelp() {
         new CommandLineRunner(new String[] {"--version", "--help"}, out, err);
         assertTrue(outContent.toString().contains("Version:"));
         assertEquals("stderr must be empty; help text must not be printed", "",
                       errContent.toString());
     }

     /**
      * An unrecognised flag before --version must not cause error text to
      * appear – version output takes priority.
      */
     public void testVersionFlagWithInvalidFlagBefore() {
         new CommandLineRunner(new String[] {"--bogus", "--version"}, out, err);
         assertTrue("Version should appear in stdout even with invalid flag",
                     outContent.toString().contains("Version:"));
         assertEquals("stderr must be empty; error must not appear", "",
                       errContent.toString());
     }

     /**
      * An unrecognised flag after --version behaves the same way.
      */
     public void testVersionFlagWithInvalidFlagAfter() {
         new CommandLineRunner(new String[] {"--version", "--bogus"}, out, err);
         assertTrue(outContent.toString().contains("Version:"));
         assertEquals("", errContent.toString());
     }

     /**
      * The printed version string must follow the expected multi-line pattern.
      */
     public void testVersionOutputPattern() {
         new CommandLineRunner(new String[] {"--version"}, out, err);
         String output = outContent.toString();
         assertTrue(output.matches(
                 "(?s)Closure Compiler \\(http://code\\.google\\.com/closure/compiler\\)\\n"
                 + "Version: .+\\nBuilt on: .+\\n?"));
     }

     /**
      * A silent guard that the compiler itself is never started when
      * --version is present.
      */
     public void testVersionDoesNotInvokeRun() {
         CommandLineRunner runner =
             new CommandLineRunner(new String[] {"--version"}, out, err);
         assertFalse("shouldRunCompiler must return false when --version is specified",
                     runner.shouldRunCompiler());
         assertTrue("Version string should appear in stdout",
                    outContent.toString().contains("Version:"));
         assertEquals("stderr must be empty", "", errContent.toString());
     }

     /**
      * Sanity check that exit-on-version can be simulated via the programmatic
      * API (main is untestable because of System.exit).
      */
     public void testProgrammaticVersionExit() {
         CommandLineRunner runner =
             new CommandLineRunner(new String[] {"--version"}, out, err);
         assertFalse(runner.shouldRunCompiler());
         assertTrue(outContent.toString().startsWith("Closure Compiler"));
         assertEquals("", errContent.toString());
     }
 }