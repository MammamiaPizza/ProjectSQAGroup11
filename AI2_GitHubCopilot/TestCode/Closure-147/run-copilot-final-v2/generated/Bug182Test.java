package com.google.javascript.jscomp;

 import junit.framework.TestCase;

 import java.util.List;

 public class Bug182Test extends TestCase {

     // --- CheckGlobalThis tests (reporting dangerous global 'this' usage) ---

     public void testIssue182a_callbackArgument() {
         // The buggy version fails to traverse functions that are direct children
         // of a CALL node, so this unsafe use of global 'this' is not reported.
         String js = "function foo() {}\n" +
                     "foo(function() { this.bar = 1; });";
         int warnings = compileAndGetGlobalThisWarnings(js);
         assertEquals("There should be one error.", 1, warnings);
     }

     public void testIssue182b_eventHandlerCallback() {
         // Another pattern where a function is passed as an argument and should
         // still be flagged when it uses global 'this'.
         String js = "function addHandler(fn) {}\n" +
                     "addHandler(function() {\n" +
                     "  this.data = 'value';\n" +
                     "});";
         int warnings = compileAndGetGlobalThisWarnings(js);
         assertEquals("There should be one error.", 1, warnings);
     }

     public void testGlobalThisInNamedFunctionAssignment() {
         // Regular assignment should still be traversed and flagged.
         String js = "var a = function() { this.x = 1; };";
         int warnings = compileAndGetGlobalThisWarnings(js);
         assertEquals("Named function assignment should report unsafe this.", 1, warnings);
     }

     public void testGlobalThisInConstructorIgnored() {
         // @constructor annotation should suppress the warning.
         String js = "/** @constructor */ function MyClass() { this.x = 1; }";
         int warnings = compileAndGetGlobalThisWarnings(js);
         assertEquals("Constructor should not report global this.", 0, warnings);
     }

     public void testGlobalThisInPrototypeMethodIgnored() {
         // Prototype method assignment should be suppressed.
         String js = "function A() {}\n" +
                     "A.prototype.m = function() { this.prop = 1; };";
         int warnings = compileAndGetGlobalThisWarnings(js);
         assertEquals("Prototype method should not report global this.", 0, warnings);
     }

     public void testGlobalThisInOverriddenMethodIgnored() {
         // @override annotation should cause traversal to be skipped.
         String js = "/** @override */\n" +
                     "MyProto.m = function() { this.x = 1; };";
         int warnings = compileAndGetGlobalThisWarnings(js);
         assertEquals("Overridden method should not report global this.", 0, warnings);
     }

     // --- RuntimeTypeCheck tests ---

     public void testValueWithInnerFn() {
         // The buggy version may insert the type check before the inner function
         // declaration or omit it entirely. The correct behavior is to insert
         // the check AFTER any inner function declarations and BEFORE the return.
         String js = "/** @return {string} */\n" +
                     "function f() {\n" +
                     "  function inner() {\n" +
                     "  }\n" +
                     "  return \"hello\";\n" +
                     "}";
         String result = compileWithRuntimeTypeCheck(js);

         // The check call must appear after the inner function definition.
         int innerIdx = result.indexOf("function inner()");
         int checkIdx = result.indexOf("$jscomp.typecheck.checkType(");
         int returnIdx = result.indexOf("return \"hello\"");

         assertTrue("Output does not contain inner function.", innerIdx != -1);
         assertTrue("Output does not contain checkType call.", checkIdx != -1);
         assertTrue("Output does not contain return statement.", returnIdx != -1);

         assertTrue("CheckType call must appear after inner function.",
                    innerIdx < checkIdx);
         assertTrue("CheckType call must appear before return statement.",
  checkIdx < returnIdx);
     }

     public void testReturnWithoutInnerFn() {
         // No inner function: check is inserted before the return.
         String js = "/** @return {number} */\n" +
                     "function f() { return 42; }";
         String result = compileWithRuntimeTypeCheck(js);
         int checkIdx = result.indexOf("$jscomp.typecheck.checkType(");
         int returnIdx = result.indexOf("return 42");
         assertTrue("CheckType call missing.", checkIdx != -1);
         assertTrue("CheckType call must appear before return.", checkIdx < returnIdx);
     }

     public void testReturnNoAnnotationsSkipsCheck() {
         // Without a known return type, no check should be inserted.
         String js = "function f() { return 42; }";
         String result = compileWithRuntimeTypeCheck(js);
         assertTrue("CheckType should not appear for unannotated return.",
                    result.indexOf("$jscomp.typecheck.checkType(") == -1);
     }

     public void testReturnNullInsertsCheck() {
         // Returning null (a known type) should get a check.
         String js = "/** @return {?} */\n" +
                     "function f() { return null; }";
         String result = compileWithRuntimeTypeCheck(js);
         assertTrue("Null return should be checked.",
                    result.indexOf("$jscomp.typecheck.checkType(") != -1);
     }

     public void testParameterCheckInserted() {
         // Verify that parameter type checks are added at the start of the
         // function body, after any inner function declarations.
         String js = "/** @param {number} x */\n" +
                     "function f(x) {\n" +
                     "  function inner() {}\n" +
                     "  return x;\n" +
                     "}";
         String result = compileWithRuntimeTypeCheck(js);
         int innerIdx = result.indexOf("function inner()");
         int paramCheckIdx = result.indexOf("$jscomp.typecheck.checkType(x");
         assertTrue("Parameter check missing.", paramCheckIdx != -1);
         assertTrue("Parameter check must be after inner function.",
                    innerIdx < paramCheckIdx);
     }

     public void testFunctionWithNoReturnOnlyParams() {
         // Should insert parameter checks but no return check.
         String js = "/** @param {string} s */\n" +
                     "function f(s) {\n" +
                     "  var x = s;\n" +
                     "}";
         String result = compileWithRuntimeTypeCheck(js);
         // Expect at least one check for the parameter.
         assertTrue("Parameter check for 's' should exist.",
                    result.indexOf("$jscomp.typecheck.checkType(s") != -1);
         // No return check, but also no return statement.
         int returnIdx = result.indexOf("return");
         assertTrue("No return check expected, there is no return statement.",
                    returnIdx == -1 || result.indexOf("checkType(", returnIdx) == -1);
     }

     // --- Helpers ---

     private int compileAndGetGlobalThisWarnings(String js) {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setWarningLevel(DiagnosticGroups.GLOBAL_THIS, CheckLevel.WARNING);
         Result result = compiler.compile(
                 SourceFile.fromCode("externs", ""),
                 SourceFile.fromCode("test", js),
                 options);
         int count = 0;
         for (JSError error : result.warnings) {
             if (error.getType().equals(CheckGlobalThis.GLOBAL_THIS)) {
                 count++;
             }
         }
         return count;
     }

     private String compileWithRuntimeTypeCheck(String js) {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         // Enable runtime type checks during compilation.
         options.runtimeTypeCheck = true;
         // Disable optimizations to keep the output structure predictable.
         options.setRemoveDeadCode(false);
         Result result = compiler.compile(
                 SourceFile.fromCode("externs", ""),
                 SourceFile.fromCode("test", js),
                 options);
         assertTrue("Compilation should succeed.", result.success);
         return compiler.toSource();
     }
 }```
