package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Arrays;
import java.util.Collections;
import junit.framework.TestCase;
import org.junit.Test;

public class FlowSensitiveInlineVariablesCatchRegressionTest extends TestCase {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new FlowSensitiveInlineVariables(compiler);
  }

  private void testSame(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Arrays.asList(SourceFile.fromCode("testcode", source)),
        options);
    compiler.parseInputs();

    Node externs = compiler.getExternsRoot();
    Node root = compiler.getJsRoot();
    String expected = compiler.toSource(root);

    getProcessor(compiler).process(externs, root);

    assertEquals(expected, compiler.toSource(root));
  }

  @Test
  public void testDoesNotInlineGetPropAssignmentOutOfTryWithReturningCatch() {
    testSame(
        "function f(a) {"
            + "var x;"
            + "try { x = a.b; }"
            + "catch (e) { return e; }"
            + "return x;"
            + "}");
  }

  @Test
  public void testDoesNotInlineGetPropVarInitializerOutOfTryWithReturningCatch() {
    testSame(
        "function f(a) {"
            + "try { var x = a.b; }"
            + "catch (e) { return e; }"
            + "return x;"
            + "}");
  }

  @Test
  public void testDoesNotInlineGetElemAssignmentOutOfTryWithReturningCatch() {
    testSame(
        "function f(a, key) {"
            + "var x;"
            + "try { x = a[key]; }"
            + "catch (e) { return e; }"
            + "return x;"
            + "}");
  }
}