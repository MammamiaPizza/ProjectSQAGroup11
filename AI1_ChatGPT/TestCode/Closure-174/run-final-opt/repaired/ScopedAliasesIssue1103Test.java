package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
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

abstract class CompilerTestCase extends junit.framework.TestCase {
  CompilerTestCase(String externs) {}

  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected void test(String input, String expected) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", input)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    getProcessor(compiler).process(null, root);
    assertEquals(0, compiler.getErrors().length);
    assertEquals(expected, compiler.toSource());
  }

  protected void testError(String input, DiagnosticType expectedError) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", input)),
        new CompilerOptions());
    Node root = compiler.parseInputs();
    getProcessor(compiler).process(null, root);
    assertEquals(1, compiler.getErrors().length);
    assertEquals(expectedError, compiler.getErrors()[0].type);
  }
}
