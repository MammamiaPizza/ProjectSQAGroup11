package com.google.javascript.jscomp;

import com.google.common.base.Supplier;
import org.junit.Test;

public class InlineObjectLiteralsRegressionTest extends CompilerTestCase {
  private int nextId;

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineObjectLiterals(
        compiler,
        new Supplier<String>() {
          @Override
          public String get() {
            return String.valueOf(nextId++);
          }
        });
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testSplitsObjectLiteralPropertyReads() {
    test(
        "function f(){var x={a:1,b:2};x.a+x.b;}",
        "function f(){"
            + "var JSCompiler_object_inline_a_0=1;"
            + "var JSCompiler_object_inline_b_1=2;"
            + "JSCompiler_object_inline_a_0+JSCompiler_object_inline_b_1;"
            + "}");
  }

  @Test
  public void testSplitsPropertyWrites() {
    test(
        "function f(){var x={a:1};x.a=2;x.a;}",
        "function f(){"
            + "var JSCompiler_object_inline_a_0=1;"
            + "JSCompiler_object_inline_a_0=2;"
            + "JSCompiler_object_inline_a_0;"
            + "}");
  }

  @Test
  public void testAssignmentExpressionInitializationIsRewritten() {
    test(
        "function f(){var x;x={a:1};x.a;}",
        "function f(){"
            + "var JSCompiler_object_inline_a_0;"
            + "JSCompiler_object_inline_a_0=1,true;"
            + "JSCompiler_object_inline_a_0;"
            + "}");
  }

  @Test
  public void testDirectAliasPreventsInlining() {
    testSame("function f(){var x={a:1};var y=x;y.a;}");
  }

  @Test
  public void testMethodCallPreventsInliningBecauseOfThisBinding() {
    testSame("function f(){var x={a:function(){return this;}};x.a();}");
  }

  @Test
  public void testSelfReferentialObjectLiteralIsNotInlined() {
    testSame("function f(){var x={a:x.a};x.a;}");
  }

  @Test
  public void testReferencedInitialValuePreventsLaterVariableFromBecomingStale() {
    test(
        "function f(){var x={a:y};var y={b:1};x.a.b;}",
        "function f(){"
            + "var JSCompiler_object_inline_a_0=y;"
            + "var y={b:1};"
            + "JSCompiler_object_inline_a_0.b;"
            + "}");
  }
}
