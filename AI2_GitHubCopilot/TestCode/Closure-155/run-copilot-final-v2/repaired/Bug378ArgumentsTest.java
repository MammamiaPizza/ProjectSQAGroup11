package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import junit.framework.TestCase;

public class Bug378ArgumentsTest extends TestCase {

  /**

 - Compiles the given JavaScript, runs the InlineVariables pass (LOCALS_ONLY, no string
 - inlining), and returns the resulting source.
    */
   private String compileAndInline(String js) {
 Compiler compiler = new Compiler();
 CompilerOptions options = new CompilerOptions();
 compiler.compile(
    JSSourceFile.fromCode("externs.js", ""),
    JSSourceFile.fromCode("test.js", js),
    options);
 Node root = compiler.getRoot();
 InlineVariables pass = new InlineVariables(
    compiler, InlineVariables.Mode.LOCALS_ONLY, false);
 pass.process(null, root);
 return compiler.toSource();
   }

  // --------------------------------------------------------------- // Test: normal inlining when
arguments is not involved. // --------------------------------------------------------------- public
void testNoArgumentsAccessInliningAllowed() {
    String js = "function f() { var x = 5; return x; } f();";
    String expected = "function f() { return 5; } f();";
    assertEquals(expected, compileAndInline(js)); }

  // --------------------------------------------------------------- // Test: arguments[0] directly
modified → parameter 'a' must not inline. //
--------------------------------------------------------------- public void
testArgumentsModifiedDirectly() {
    String js = "function f(a) { arguments[0] = 1; return a; } f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }

  // --------------------------------------------------------------- // Test: conditional
modification of arguments[0] still blocks inlining. //
--------------------------------------------------------------- public void
testArgumentsModifiedInIf() {
    String js = "function f(a) { if (true) arguments[0] = 1; return a; } f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }

  // --------------------------------------------------------------- // Test: arguments passed to
another function (escape) prevents inlining. //
--------------------------------------------------------------- public void
testArgumentsPassedToFunction() {
    String js = "function g(args) { args[0] = 2; }" +
                "function f(a) { g(arguments); return a; } f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }

  // --------------------------------------------------------------- // Test: inner function
modifies the outer arguments[0]. // ---------------------------------------------------------------
public void testArgumentsModifiedInInnerFunction() {
    String js = "function f(a) {" +
                "  function inner() { arguments[0] = 1; }" +
                "  inner();" +
                "  return a;" +
                "} f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }

  // --------------------------------------------------------------- // Test: aliasing arguments to
a local variable and then modifying it. //
--------------------------------------------------------------- public void
testAliasedArgumentsModified() {
    String js = "function f(a) {" +
                "  var args = arguments;" +
                "  args[0] = 1;" +
                "  return a;" +
                "} f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }

  // --------------------------------------------------------------- // Test: for..in loop over
arguments (escape). // --------------------------------------------------------------- public void
testArgumentsUsedInForIn() {
    String js = "function f(a) {" +
                "  for (var p in arguments) { var x = arguments[p]; }" +
                "  return a;" +
                "} f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }

  // --------------------------------------------------------------- // Test: arguments assigned to
a variable, then that variable is passed. //
--------------------------------------------------------------- public void
testArgumentsAssignedToVarThenPassed() {
    String js = "function g(args) { args[0] = 2; }" +
                "function f(a) { var args = arguments; g(args); return a; } f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }

  // --------------------------------------------------------------- // Test: modification of
arguments[0] after a return (still flagged). //
--------------------------------------------------------------- public void
testArgumentsModifiedAfterReturn() {
    String js = "function f(a) {" +
                "  var t = a;" +
                "  arguments[0] = 1;" +
                "  return t;" +
                "} f(1);";
    // The variable t should not be inlined because it depends on a which is aliased.
    String result = compileAndInline(js);
    assertTrue(result.contains("return t")); }

  // --------------------------------------------------------------- // Test: dead code contains
arguments modification → still blocks inlining. //
--------------------------------------------------------------- public void
testArgumentsUsedInDeadCode() {
    String js = "function f(a) {" +
                "  if (false) { arguments[0] = 1; }" +
                "  return a;" +
                "} f(1);";
    String result = compileAndInline(js);
    assertTrue("Parameter 'a' must not be inlined",
               result.contains("return a")); }
}
