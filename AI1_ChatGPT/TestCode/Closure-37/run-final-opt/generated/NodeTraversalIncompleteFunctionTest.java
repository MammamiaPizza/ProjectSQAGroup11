package com.google.javascript.jscomp;

import com.google.javascript.rhino.SourceFile;
import java.util.Collections;
import junit.framework.TestCase;

public class NodeTraversalIncompleteFunctionTest extends TestCase {

  private Compiler compile(String source) {
    Compiler compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("test.js", source)),
        options);
    return compiler;
  }

  public void testCompleteFunctionCompilesWithoutErrors() {
    Compiler compiler = compile(
        "function complete(value) { return value + 1; }\n"
        + "complete(0);");

    assertEquals(0, compiler.getErrorCount());
  }

  public void testFunctionDeclarationMissingParameterListDoesNotCrash() {
    Compiler compiler = compile("function incomplete(");

    assertTrue("Incomplete syntax should be reported as a parse error",
        compiler.getErrorCount() > 0);
  }

  public void testFunctionDeclarationMissingBodyDoesNotCrash() {
    Compiler compiler = compile("function incomplete()");

    assertTrue("A function without a body should be reported as invalid",
        compiler.getErrorCount() > 0);
  }

  public void testAnonymousFunctionExpressionMissingBodyDoesNotCrash() {
    Compiler compiler = compile("var value = function()");

    assertTrue("An incomplete function expression should be reported as invalid",
        compiler.getErrorCount() > 0);
  }

  public void testFunctionDeclarationMissingNameDoesNotCrash() {
    Compiler compiler = compile("function () {}");

    assertTrue("A nameless function declaration should be reported as invalid",
        compiler.getErrorCount() > 0);
  }
}
