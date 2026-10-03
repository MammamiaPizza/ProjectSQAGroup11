package com.google.javascript.jscomp;

import org.junit.Test;

public class ScopedAliasesIssue1103Test extends CompilerTestCase {

  public ScopedAliasesIssue1103Test() {
    super("");
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new ScopedAliases(compiler, null, null);
  }

  @Test
  public void testAliasMayReferToLaterAliasDeclaration() {
    test(
        "goog.scope(function() {"
            + "var a = b;"
            + "var b = goog.b;"
            + "a.c;"
            + "});",
        "goog.b.c;");
  }

  @Test
  public void testAliasMayReferToLaterAliasInSameVarStatement() {
    test(
        "goog.scope(function() {"
            + "var a = b, b = goog.b;"
            + "a.c;"
            + "});",
        "goog.b.c;");
  }

  @Test
  public void testAliasMayReferToEarlierAliasDeclaration() {
    test(
        "goog.scope(function() {"
            + "var b = goog.b;"
            + "var a = b;"
            + "a.c;"
            + "});",
        "goog.b.c;");
  }

  @Test
  public void testDirectAliasStillExpandsNormally() {
    test(
        "goog.scope(function() {"
            + "var a = goog.b;"
            + "a.c;"
            + "});",
        "goog.b.c;");
  }

  @Test
  public void testNonAliasLocalIsStillRejected() {
    testError(
        "goog.scope(function() {"
            + "var a = 1;"
            + "a++;"
            + "});",
        ScopedAliases.GOOG_SCOPE_NON_ALIAS_LOCAL);
  }
}
