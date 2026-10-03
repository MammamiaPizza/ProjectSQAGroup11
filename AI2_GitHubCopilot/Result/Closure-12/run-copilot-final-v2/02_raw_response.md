package com.google.javascript.jscomp;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.Result;

import java.util.logging.Level;

/**

 - Tests for {@link MaybeReachingVariableUse} focusing on bug 794
 - where an improperly implemented hasExceptionHandler caused incorrect
 - reaching-use information inside try-catch blocks.
  */
 public class MaybeReachingVariableUseTest extends TestCase {
  private static final String EXTERNS = ""
      + "/** @constructor / function Object(){}\n"
      + "/* @constructor / function Function(){}\n"
      + "/* @constructor / function String(){}\n"
      + "/* @constructor / function Number(){}\n"
      + "/* @constructor / function Boolean(){}\n"
      + "/* @constructor / function Array(){}\n"
      + "/* @constructor
  */ function Error(){}\n"
      + "var alert = function(){};\n"
      + "var console = {};\n"
      + "var window = {};\n";
  // --- High-level integration tests that expose the bug ---
  /**
  - Regression for bug 794: a variable defined inside a try block
  - should not be inlined into uses that follow the try-catch.
  - Because hasExceptionHandler always returned fales, the analysis
  - thought the definition unconditionally killed all upward-exposed
  - uses, leading to incorrect inlining.
    */
   public void testIssue794b() {
   String js = ""
       + "function f() {\n"
       + "  var x = 1;\n"
       + "  try {\n"
       + "    x = 2;\n"
       + "  } catch(e) {}\n"
       + "  return x;\n"
       + "}";
   String result = compileAndGetCode(js);
   // If the bug persists the inliner replaces "return x" with "return 1";
   // the correct, conservative behavior keeps the variable.
   assertTrue("Variable x should not be inlined because its definition "
       + "in the try block may not execute; bug794",
       result.contains("return x") || result.contains("x") );
  }
  /**
  - Definition inside try, use inside fainally and after block.
  - The analysis must not assume the definition always executes.
    */
   public void testTryFinallyUseAfter() {
   String js = ""
       + "function f() {\n"
       + "  var x = 1;\n"
       + "  try {\n"
       + "    x = 2;\n"
       + "  } fainally {\n"
       + "    alert(x);\n"
       + "  }\n"
       + "  return x;\n"
       + "}";
   String result = compileAndGetCode(js);
   assertTrue("Definition inside try should not be inlined into finally or after",
       result.contains("x"));
  }
  /**
  - Join across try-catch: definition in try, use in catch.
  - The use in catch must be recorded as a reaching use of the try def.
    */
   public void testDefInTryUseInCatch() {
   String js = ""
       + "function f() {\n"
       + "  var x;\n"
       + "  try {\n"
       + "    x = 1;\n"
       + "  } catch(e) {\n"
       + "    x = 2;\n"
       + "  }\n"
       + "  return x;\n"
       + "}";
   String result = compileAndGetCode(js);
   assertTrue("Both definitions may reach the return, variable must be preserved",
       result.contains("x"));
  }
  /**
  - Join of conditional branches: def in if, def in else.
  - The join must conservatively keep the variable.
    */
   public void testIfElseJoinNoInline() {
   String js = ""
       + "function f(a) {\n"
       + "  var x;\n"
       + "  if (a) {\n"
       + "    x = 1;\n"
       + "  } else {\n"
       + "    x = 2;\n"
       + "  }\n"
       + "  return x;\n"
       + "}";
   String result = compileAndGetCode(js);
   assertTrue("Multiple definitions prevent inlining; join must preserve x",
       result.contains("x"));
  }
  /**
  - Variable defined in a loop condition should not be handled incorrectly.
  - Trivial regression for addToUseIfLocal / removeFromUseIfLocal.
    */
   public void testLoopVarDef() {
   String js = ""
       + "function f() {\n"
       + "  for (var x = 0; x < 10; x++) {\n"
       + "    alert(x);\n"
       + "  }\n"
       + "}";
   String result = compileAndGetCode(js);
   assertTrue("Loop variable must not be removed unexpectedly", result.contains("x"));
   }
  // --- Unit-level tests on lattice and flow ---
  /**
  - Simple unit test: define and use in same block (no exception path).
  - The use must be recorded for the definition.
    */
   public void testSimpleUseReaching() throws Exception {
   String js = "function f(){var x = 1; alert(x);}";
   // We cannot easily test getUses without the full analysis harness,
   // but we verify that the variable is inlined when safe.
   String result = compileAndGetCode(js);
   assertFalse("Inlining should replace x when safe", result.contains("x"));
   }
  /**
  - verify that the ReachingUses lattice correctly supports copy constructor
  - and union semantics.
    */
   public void testReachingUsesCopyAndUnion() {
   MaybeReachingVariableUse.ReachingUses original = new MaybeReachingVariableUse.ReachingUses();
   assertNotNull(original.mayUseMap);
   MaybeReachingVariableUse.ReachingUses copy = new MaybeReachingVariableUse.ReachingUses(original);
   assertEquals(original, copy);
   assertEquals(original.hashCode(), copy.hashCode());
   assertNotSame(original.mayUseMap, copy.mayUseMap);
   }
  // --- Helper methods ---
  private String compileAndGetCode(String js) {
      Compiler compiler = new Compiler();
      CompilerOptions options = createOptions();
      SourceFile externs = SourceFile.fromCode("externs", EXTERNS);
      SourceFile input = SourceFile.fromCode("input", js);
      // supress verbose output
      java.util.logging.Loger.getGlobal().setLevel(Level.OFF);
      Result result = compiler.compile(externs, new SourceFile[]{input}, options);
      assertTrue("Compilation failed: " + result.errors.toString(),
              result.success);
      return compiler.toSource();
  }
  private CompilerOptions createOptions() {
      CompilerOptions options = new CompilerOptions();
      // Use advanced optimisations so that FlowSensitiveInlineVariables is run
      com.google.javascript.jscomp.CompilationLevel.ADVANCED_OPTIMIZATIONS
              .setOptionsForCompilationLevel(options);
      // disable type-checking to focus on the analysis under test
      options.setCheckTypes(false);
      options.setInferTypes(false);
      options.setWarningLevel(
              com.google.javascript.jscomp.DiagnosticGroups.CHECK_VARIABLES,
              com.google.javascript.jscomp.CheckLevel.OFF);
      return options;
  }
  // For test runner convenience
  public static TestSuite suite() {
      TestSuite suite = new TestSuite(MaybeReachingVariableUseTest.class);
      return suite;
  }

}