package com.google.javascript.jscomp;

import junit.framework.TestCase;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CommandLineRunnerVersionTest extends TestCase {

 private CommandLineRunner createRunner(String[] args,
 ByteArrayOutputStream outBytes, ByteArrayOutputStream errBytes) {
 PrintStream out = new PrintStream(outBytes);
 PrintStream err = new PrintStream(errBytes);
 return new CommandLineRunner(args, out, err);
 }

 public void testVersionFlag() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version"};
     CommandLineRunner runner = createRunner(args, out, err);
     assertFalse("should not run compiler when version is requested",

runner.shouldRunCompiler());
        String errStr = err.toString();
        assertFalse("should not print usage with --version", errStr.contains("Usage:"));
        assertTrue("should print version info", errStr.length() > 0);
        assertTrue("output should indicate compiler version",
errStr.toLowerCase().contains("compiler"));
    }

 public void testVersionFlagWithJsArg() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version", "--js", "dummy.js"};
     CommandLineRunner runner = createRunner(args, out, err);
     assertFalse(runner.shouldRunCompiler());
     String errStr = err.toString();
     assertFalse(errStr.contains("Usage:"));
     assertTrue(errStr.length() > 0);
 }

 public void testVersionFlagWithOtherValidArgs() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version", "--externs", "dummy.js", "--compilation_level",

"SIMPLE_OPTIMIZATIONS"};
        CommandLineRunner runner = createRunner(args, out, err);
        assertFalse(runner.shouldRunCompiler());
        assertFalse(err.toString().contains("Usage:"));
    }

 public void testVersionFlagOverridesHelp() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version", "--help"};
     CommandLineRunner runner = createRunner(args, out, err);
     assertFalse(runner.shouldRunCompiler());
     String errStr = err.toString();
     assertFalse(errStr.contains("Usage:"));
     assertTrue(errStr.length() > 0);
 }

 public void testSingleDashVersionIsNotRecognized() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"-version"};
     CommandLineRunner runner = createRunner(args, out, err);
     String errStr = err.toString();
     assertTrue("should print usage because -version is unknown", errStr.contains("Usage:"));
     assertFalse(errStr.toLowerCase().contains("compiler version"));
 }

 public void testAbsentVersionFlag() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {};
     CommandLineRunner runner = createRunner(args, out, err);
     assertFalse(err.toString().toLowerCase().contains("compiler version"));
     // shouldRunCompiler may be true; no assertion on it as it depends on presence of input files
 }

 public void testUnknownFlag() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--unknown_flag"};
     CommandLineRunner runner = createRunner(args, out, err);
     String errStr = err.toString();
     assertTrue("unknown flag should print usage", errStr.contains("Usage:"));
     assertFalse("unknown flag should not print version",

errStr.toLowerCase().contains("compiler version"));
    }

 public void testHelpFlag() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--help"};
     CommandLineRunner runner = createRunner(args, out, err);
     String errStr = err.toString();
     assertTrue("help should print usage", errStr.contains("Usage:"));
     assertFalse("help should not print version", errStr.toLowerCase().contains("compiler
version"));
     assertFalse(runner.shouldRunCompiler());
 }

 public void testVersionFlagOutputsToErrorStream() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version"};
     createRunner(args, out, err);
     assertTrue("Version output should appear in error stream", err.toString().length() > 0);
     String outStr = out.toString();
     assertFalse("Standard output should not contain version details",

outStr.toLowerCase().contains("compiler version"));
    }

 public void testNoCompileWhenVersionPresent() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version", "--js", "foo.js"};
     CommandLineRunner runner = createRunner(args, out, err);
     assertFalse("should not run compiler when version is requested",

runner.shouldRunCompiler());
    }

 public void testVersionStringNotEmpty() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version"};
     createRunner(args, out, err);
     String output = err.toString().trim();
     assertTrue("Version output must not be empty", output.length() > 0);
 }

 public void testMultipleVersionFlags() {
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ByteArrayOutputStream err = new ByteArrayOutputStream();
     String[] args = {"--version", "--version"};
     CommandLineRunner runner = createRunner(args, out, err);
     assertFalse(runner.shouldRunCompiler());
     String errStr = err.toString();
     assertFalse(errStr.contains("Usage:"));
     assertTrue(errStr.length() > 0);
 }

}