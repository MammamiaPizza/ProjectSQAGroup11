package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import java.util.ArrayList;
 import java.util.List;

 public class CompilerErrorCountTest extends TestCase {

  private JSSourceFile sourceFile(String name, String code) {
      return JSSourceFile.fromCode(name, code);
  }

  private Compiler compile(List<JSSourceFile> externs,
                           List<JSSourceFile> inputs,
                           CompilerOptions options) {
      Compiler compiler = new Compiler();
      compiler.compile(externs, inputs, options);
      return compiler;
  }

  private CompilerOptions strictOptions() {
      CompilerOptions opts = new CompilerOptions();
      opts.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
      return opts;
  }

  // Test that multiple inputs with strict mode violations produce
  // a positive error count (not -1) and that it matches the length
  // of getErrors().
  public void testMultipleInputsStrictErrors() {
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      inputs.add(sourceFile("a.js", "function f(a,a) {'use strict';}"));
      inputs.add(sourceFile("b.js", "function g(b,b) {'use strict';}"));
      inputs.add(sourceFile("c.js", "function h(c,c) {'use strict';}"));
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      Compiler compiler = compile(externs, inputs, strictOptions());
      int count = compiler.getErrorCount();
      assertTrue("Error count must not be negative", count >= 0);
      assertEquals("Error count should equal number of errors array",
                   count, compiler.getErrors().length);
      // 3 files, each with 1 duplicate-param error => 3 errors
      assertEquals("Expected exactly 3 errors", 3, count);
  }

  // Single input with a strict error should yield count 1.
  public void testSingleInputStrictError() {
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      inputs.add(sourceFile("a.js", "function f(a,a) {'use strict';}"));
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      Compiler compiler = compile(externs, inputs, strictOptions());
      assertEquals("Expected 1 error for single input", 1, compiler.getErrorCount());
  }

  // Inputs without any violations should produce error count 0.
  public void testZeroErrors() {
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      inputs.add(sourceFile("a.js", "function f(a) {}"));
      inputs.add(sourceFile("b.js", "function g(b) {}"));
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      Compiler compiler = compile(externs, inputs, strictOptions());
      assertEquals("Expected 0 errors for clean inputs", 0, compiler.getErrorCount());
      assertEquals(0, compiler.getErrors().length);
  }

  // An empty input list should produce 0 errors.
  public void testEmptyInputs() {
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      Compiler compiler = compile(externs, inputs, strictOptions());
      assertEquals("Empty inputs should yield 0 errors", 0, compiler.getErrorCount());
  }

  // Many inputs (50) with strict errors should not overflow to -1.
  public void testLargeNumberOfInputs() {
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      for (int i = 0; i < 50; i++) {
          inputs.add(sourceFile("in" + i + ".js",
                                "function f" + i + "(x,x) {'use strict';}"));
      }
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      Compiler compiler = compile(externs, inputs, strictOptions());
      int count = compiler.getErrorCount();
      assertTrue("Error count must not be negative for many inputs", count >= 0);
      assertEquals(50, count);
      assertEquals(count, compiler.getErrors().length);
  }

  // After compilation, getErrorCount() and getErrors() must be consistent.
  public void testErrorCountMatchesErrorsLength() {
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      inputs.add(sourceFile("a.js", "function f(a,a) {'use strict';}"));
      inputs.add(sourceFile("b.js", "'use strict'; with({}) {}"));
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      Compiler compiler = compile(externs, inputs, strictOptions());
      int errorCount = compiler.getErrorCount();
      assertTrue("Error count must be non-negative", errorCount >= 0);
      assertEquals("getErrorCount must equal length of getErrors()",
                   errorCount, compiler.getErrors().length);
  }

  // Default compiler after construction should return 0 from getErrorCount
  // without throwing a NullPointerException (error manager properly initialized).
  public void testGetErrorCountBeforeCompile() {
      Compiler compiler = new Compiler(System.out);
      int count = compiler.getErrorCount();
      assertTrue("Error count before any compilation must be >= 0", count >= 0);
  }

  // Setting an explicit ErrorManager should work and not break error counting.
  public void testSetErrorManagerAndCompile() {
      final int[] reported = {0};
      ErrorManager manager = new com.google.javascript.jscomp.BasicErrorManager() {
          @Override
          public void report(CheckLevel level, JSError error) {
              reported[0]++;
              super.report(level, error);
          }
          @Override
          public void println(CheckLevel level, JSError error) { }
          @Override
          protected void printSummary() { }
      };

      Compiler compiler = new Compiler();
      compiler.setErrorManager(manager);
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      inputs.add(sourceFile("a.js", "function f(a,a) {'use strict';}"));
      compiler.compile(externs, inputs, strictOptions());

      assertTrue("Custom error manager must have received errors", reported[0] > 0);
      int errorCount = compiler.getErrorCount();
      assertTrue("Error count must be non-negative with custom manager", errorCount >= 0);
      assertEquals("Error count must match errors array length after compile",
                   errorCount, compiler.getErrors().length);
  }

  // Two different sets of errors: strict-mode octal literal.
  // Verify count matches the sum across inputs.
  public void testMixedErrors() {
      List<JSSourceFile> inputs = new ArrayList<JSSourceFile>();
      inputs.add(sourceFile("a.js", "function f(a,a) {'use strict';}"));
      inputs.add(sourceFile("b.js", "'use strict'; var x = 012;"));
      inputs.add(sourceFile("c.js", "'use strict'; with({}) {}"));
      List<JSSourceFile> externs = new ArrayList<JSSourceFile>();
      Compiler compiler = compile(externs, inputs, strictOptions());
      int count = compiler.getErrorCount();
      assertTrue("Error count must be >= 0 for mixed violations", count >= 0);
      assertEquals("Error count should equal number of reported errors",
                   count, compiler.getErrors().length);
      // all inputs contain at least one error; count must be at least 3
      assertTrue("At least 3 errors expected", count >= 3);
  }

 }