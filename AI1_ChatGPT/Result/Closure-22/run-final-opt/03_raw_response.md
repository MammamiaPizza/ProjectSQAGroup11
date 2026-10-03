package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public class CheckSideEffectsGeneratedTest extends CheckSideEffectsGeneratedTestBase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new CheckSideEffects(compiler, CheckLevel.WARNING, false);
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testStandaloneNameProducesWarning() {
    testWarning("var x; x;", CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testUnusedEqualityWithCallProducesWarning() {
    testWarning(
        "var x; x == foo();",
        CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testStandaloneStringProducesWarning() {
    testWarning(
        "'continued on the next line';",
        CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testStandaloneNumberProducesWarning() {
    testWarning("0;", CheckSideEffects.USELESS_CODE_ERROR);
  }

  @Test
  public void testCallExpressionIsNotReportedAsUseless() {
    testSame("foo();");
  }

  @Test
  public void testEmptyStatementsAreAllowed() {
    testSame(";;");
  }

  @Test
  public void testIndirectEvalCalleeCommaExpressionIsPreserved() {
    testSame("(0, eval)('x');");
  }
}

abstract class CheckSideEffectsGeneratedTestBase extends org.junit.Assert {
  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected int getNumRepetitions() {
    return 1;
  }

  protected void testWarning(String js, DiagnosticType warning) {
    test(js, warning);
  }

  protected void testSame(String js) {
    test(js, null);
  }

  private void test(String js, DiagnosticType expectedWarning) {
    for (int i = 0; i < getNumRepetitions(); i++) {
      Compiler compiler = new Compiler();
      compiler.init(
          Collections.<SourceFile>emptyList(),
          Collections.singletonList(SourceFile.fromCode("testcode", js)),
          new CompilerOptions());

      Node root = compiler.parseInputs();
      Node externs = root.getFirstChild();
      Node source = externs.getNext();
      String before = source.toStringTree();

      getProcessor(compiler).process(externs, source);

      assertEquals(before, source.toStringTree());
      JSError[] warnings = compiler.getWarnings();
      if (expectedWarning == null) {
        assertEquals(0, warnings.length);
      } else {
        assertEquals(1, warnings.length);
        assertEquals(expectedWarning, warnings[0].type);
      }
    }
  }
}