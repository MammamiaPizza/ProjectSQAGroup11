package com.google.javascript.jscomp;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.google.javascript.jscomp.CompilerPass;
import com.google.javascript.jscomp.CompilerTestCase;

public class FlowSensitiveInlineVariablesTest extends CompilerTestCase {

  private boolean useUpdateCompatibility;

  public FlowSensitiveInlineVariablesTest() {
    super();
    // Enable the "change" checking that validates that the AST matches the output.
    this.enableAstValidation(true); }

  @Override public void setUp() throws Exception {
    super.setUp();
    useUpdateCompatibility = compiler.getOptions().shouldUseUpdateCompatibility(); }

  @Override CompilerPass getProcessor(final Compiler compiler) {
    return new FlowSensitiveInlineVariables(compiler); }

  // ---------- normal inlining ---------- @Test public void testSimpleInline() {
    // single def, single use – should inline
    test("function f() { var x = 1; return x; }",
         "function f() { return 1; }"); }

  @Test public void testDoNotInlineMultipleUses() {
    // two uses – not safe to inline
    testSame("function f() { var x = 1; return x + x; }"); }

  @Test public void testDoNotInlineSideEffectBeforeUse() {
    // assignment has side effect – must not inline
    testSame("function f() { var x = 1; x = foo(); alert(x); }"); }

  // ---------- catch-block related tests (fix for bug #864) ---------- @Test public void
testDoNotInlineCatchExpression1() {
    // var defined before try, only use in catch
    testSame("function f() { var x = 1; try {} catch(e) { alert(x); } }"); }

  @Test public void testDoNotInlineCatchExpression1a() {
    // catch without parameter binding (ES2019 optional catch)
    testSame("function f() { var x = 1; try {} catch { alert(x); } }"); }

  @Test public void testDoNotInlineCatchExpression1b() {
    // var defined inside try, only use in catch
    testSame("function f() { try { var x = 1; } catch(e) { alert(x); } }"); }

  @Test public void testDoNotInlineCatchExpression2() {
    // var defined before try, used both in catch and after try
    testSame("function f() { var x = 1; try {} catch(e) { alert(x); } return x; }"); }

  @Test public void testDoNotInlineCatchExpression3() {
    // nested catch – use in inner catch
    testSame("function f() { var x = 1; try {} catch(e) { try {} catch(ex) { alert(x); } } }"); }

  @Test public void testDoNotInlineCatchParamMask() {
    // catch parameter name masks outer variable
    testSame("function f() { var x = 1; try {} catch(x) { alert(x); } }"); }

  @Test public void testDoNotInlineFinallyBlockUse() {
    // var defined inside try, used in finally – may not be assigned when finally runs
    testSame("function f() { try { var x = 1; } finally { alert(x); } }"); }

  @Test public void testDoNotInlineMultipleCatchUses() {
    // var defined before try, used in multiple catch blocks
    testSame(
      "function f() { var x = 1; try {} catch(e) { alert(x); } catch(ex) { alert(x); } }"); }

  @Test public void testInlineSafeOutsideTryCatch() {
    // var defined and used completely outside the try-catch – safe to inline
    test("function f() { var x = 1; return x; try {} catch(e) {} }",
         "function f() { return 1; try {} catch(e) {} }"); }
}
