package com.google.javascript.jscomp;

import junit.framework.TestCase;

public class TypeCheckBug66Test extends TestCase {

  public void testGetTypedPercentFullyTypedVar() {
    assertTypedPercent("/** @type {number} */ var x = 1;", 100.0); }

  public void testGetTypedPercentPropertyAssignment() {
    assertTypedPercent(
        "/** @type {{a: number}} */ var obj = {}; obj.a = 1;",
        100.0); }

  public void testGetTypedPercentFunctionCall() {
    assertTypedPercent(
        "/** @param {number} x\n @return {number} */\n" +
        "function f(x) { return x; }\n" +
        "f(1);",
        100.0); }

  public void testGetTypedPercentVarDeclarations() {
    assertTypedPercent(
        "/** @type {number} / var a = 1;\n" +
        "/* @type {string} / var b = 's';\n" +
        "/* @type {boolean} */ var c = true;",
        100.0); }

  public void testGetTypedPercentNestedCalls() {
    assertTypedPercent(
        "/** @return {number} / function g() { return 1; }\n" +
        "/* @param {number} x\n @return {number} */ function f(x) { return x; }\n" +
        "f(g());",
        100.0); }

  public void testGetTypedPercentPropertyReadAfterAssignment() {
    assertTypedPercent(
        "/** @type {{a: number}} */ var obj = {};\n" +
        "obj.a = 1;\n" +
        "var b = obj.a;",
        100.0); }

  public void testGetTypedPercentEmptyFunctionCall() {
    assertTypedPercent(
        "/** @return {void} */ function f() {}\n" +
        "f();",
        100.0); }

  private void assertTypedPercent(String js, double expected) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    options.checkTypes = true;
    options.inferTypes = false;

 SourceFile externs = SourceFile.fromCode("externs.js", "");
 SourceFile source = SourceFile.fromCode("input.js", js);
 compiler.compile(externs, source, options);

 TypeCheck typeCheck = compiler.getTypeCheck();
 assertNotNull("TypeCheck pass should have run", typeCheck);
 assertEquals(expected, typeCheck.getTypedPercent(), 0.001); }

}
