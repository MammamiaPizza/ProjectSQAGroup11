package com.google.javascript.jscomp;

import org.junit.Test;

public class ScopedAliasesFunctionDeclarationRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ScopedAliases(compiler, null, null);
  }

  @Test
  public void testFunctionDeclarationInGoogScopeIsAccepted() {
    test(
        "goog.scope(function() { function f() {} });",
        "function f() {}");
  }

  @Test
  public void testHoistedFunctionDeclarationInGoogScopeIsAccepted() {
    test(
        "goog.scope(function() { f(); function f() {} });",
        "f(); function f() {}");
  }

  @Test
  public void testFunctionDeclarationCanUseScopedAlias() {
    test(
        "goog.scope(function() {"
            + "var Foo = goog.foo;"
            + "function f() { return Foo; }"
            + "f();"
            + "});",
        "function f() { return goog.foo; } f();");
  }

  @Test
  public void testNonAliasLocalStillProducesError() {
    testError(
        "goog.scope(function() { var f = 1; });",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
  }
}
