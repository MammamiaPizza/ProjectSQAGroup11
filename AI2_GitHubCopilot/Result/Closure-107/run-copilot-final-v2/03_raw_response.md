package com.google.javascript.jscomp;

 import com.google.common.collect.Lists;

 import junit.framework.TestCase;

 import java.util.List;

 /**
  * Tests for CommandLineRunner focusing on warning-guard wiring in createOptions()
  * and WarningGuardSetter.addValue().  Reproduces the getMsg wiring bug (#1135).
  */
 public class CommandLineRunnerTest extends TestCase {

     /** Subclass that gives test access to protected createOptions / createCompiler. */
     private static class TestRunner extends CommandLineRunner {
         TestRunner(String[] args) {
             super(args);
         }

         @Override
         public CompilerOptions createOptions() {
             return super.createOptions();
         }

         @Override
         public Compiler createCompiler() {
             return super.createCompiler();
         }
     }

     // ---------- fault-related behaviour ----------

     /** Reproduces trigger failure: getMsg wiring must not produce spurious warnings. */
     public void testGetMsgWiringNoWarnings() throws Exception {
         TestRunner runner = new TestRunner(new String[] {});
         CompilerOptions options = runner.createOptions();
         Compiler compiler = runner.createCompiler();

         SourceFile input = SourceFile.fromCode("test.js", "goog.getMsg('hello');");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         compiler.compile(externs, Lists.newArrayList(input), options);

         assertEquals(0, compiler.getErrors().length);
         assertEquals(0, compiler.getWarnings().length);
     }

     /** Guard-off for a real warning group must make warnings disappear. */
     public void testGuardOffSuppressesWarning() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var a = b;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         // Reference run – expect warning(s)
         TestRunner ref = new TestRunner(new String[] {});
         Compiler refCompiler = ref.createCompiler();
         refCompiler.compile(externs, Lists.newArrayList(input), ref.createOptions());
         assertTrue("Expected at least one warning without guard",
                 refCompiler.getWarnings().length > 0);

         // With --jscomp_off checkVars the undeclared-variable warning should be gone
         TestRunner guarded = new TestRunner(new String[] {"--jscomp_off", "checkVars"});
         Compiler guardedCompiler = guarded.createCompiler();
         guardedCompiler.compile(externs, Lists.newArrayList(input), guarded.createOptions());
         assertEquals(0, guardedCompiler.getWarnings().length);
         assertEquals(0, guardedCompiler.getErrors().length);
     }

     /** Guard-error must turn the same kind of warning into an error. */
     public void testGuardErrorUpgradesToError() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var a = b;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {"--jscomp_error", "checkVars"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertTrue("Expected at least one error after upgrading", compiler.getErrors().length > 0);
         assertEquals(0, compiler.getWarnings().length);
     }

     /** Guard-warning must leave the diagnostic as a warning (not off, not error). */
     public void testGuardWarningKeepsWarning() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var a = b;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {"--jscomp_warning", "checkVars"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertTrue("Expected at least one warning", compiler.getWarnings().length > 0);
         assertEquals(0, compiler.getErrors().length);
     }

     /** Warning level QUIET must suppress all warnings. */
     public void testWarningLevelQuietNoWarnings() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var a = b;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {"--warning_level", "QUIET"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertEquals(0, compiler.getWarnings().length);
         assertEquals(0, compiler.getErrors().length);
     }

     // ---------- normal / boundary / robustness ----------

     public void testEmptyArgsNoWarnings() throws Exception {
         TestRunner runner = new TestRunner(new String[] {});
         CompilerOptions options = runner.createOptions();
         Compiler compiler = runner.createCompiler();

         SourceFile input = SourceFile.fromCode("test.js", "var x = 1;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         compiler.compile(externs, Lists.newArrayList(input), options);
         assertEquals(0, compiler.getErrors().length);
         assertEquals(0, compiler.getWarnings().length);
     }

     /** Multiple --jscomp_off flags with different groups all take effect. */
     public void testMultipleGuardFlags() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var a = b; var c = d;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {
                 "--jscomp_off", "checkVars", "--jscomp_off", "missingProperties"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertEquals(0, compiler.getWarnings().length);
         assertEquals(0, compiler.getErrors().length);
     }

     /** Duplicate flags for the same group do not crash or leak warnings. */
     public void testDuplicateFlagsNoCrash() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var a = b;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {
                 "--jscomp_off", "checkVars", "--jscomp_off", "checkVars"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertEquals(0, compiler.getWarnings().length);
     }

     /** An unknown warning group name is silently ignored; compilation succeeds. */
     public void testInvalidWarningGroupIgnored() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var x = 1;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {"--jscomp_off", "nonExistentGroup"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertEquals(0, compiler.getErrors().length);
         assertEquals(0, compiler.getWarnings().length);
     }

     /** Code with multiple warnings is clean when all relevant groups are turned off. */
     public void testBoundaryMultipleWarningsSuppressed() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js",
                 "var a = b; function f() { var c = d; }");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {"--jscomp_off", "checkVars"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertEquals(0, compiler.getWarnings().length);
         assertEquals(0, compiler.getErrors().length);
     }

     /** --warning_level VERBOSE with a clean script still yields no diagnostics. */
     public void testWarningLevelVerboseWithCleanCode() throws Exception {
         SourceFile input = SourceFile.fromCode("test.js", "var x = 1;");
         List<SourceFile> externs = CommandLineRunner.getDefaultExterns();

         TestRunner runner = new TestRunner(new String[] {"--warning_level", "VERBOSE"});
         Compiler compiler = runner.createCompiler();
         compiler.compile(externs, Lists.newArrayList(input), runner.createOptions());

         assertEquals(0, compiler.getWarnings().length);
         assertEquals(0, compiler.getErrors().length);
     }
 }