package com.google.javascript.jscomp;

import com.google.common.base.Supplier;
import org.junit.Test;

public class InlineObjectLiteralsRegressionTest extends CompilerTestCase {

  public InlineObjectLiteralsRegressionTest() {
    super(DEFAULT_EXTERNS);
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineObjectLiterals(
        compiler,
        new Supplier<String>() {
          private int id;

          @Override
          public String get() {
            return String.valueOf(id++);
          }
        });
  }

  @Test
  public void testDoesNotInlineDeletedPropertyRead() {
    testSame("function f(){var x={a:1};delete x.a;return x.a;}");
  }

  @Test
  public void testDoesNotInlineDeleteExpressionResult() {
    testSame("function f(){var x={a:1};return delete x.a;}");
  }

  @Test
  public void testDoesNotInlineWhenDeletingOneOfSeveralProperties() {
    testSame("function f(){var x={a:1,b:2};delete x.a;return x.b;}");
  }

  @Test
  public void testDoesNotInlineWhenDeletingMissingProperty() {
    testSame("function f(){var x={a:1};delete x.b;return x.a;}");
  }

  @Test
  public void testInlinesStablePropertyRead() {
    test(
        "function f(){var x={a:1};return x.a;}",
        "function f(){var JSCompiler_object_inline_a_0=1;"
            + "return JSCompiler_object_inline_a_0;}");
  }

  @Test
  public void testDoesNotInlineUnknownPropertyRead() {
    testSame("function f(){var x={a:1};return x.b;}");
  }

  @Test
  public void testDoesNotInlineMethodCallTarget() {
    testSame("function f(){var x={m:function(){return this;}};return x.m();}");
  }

  @Test
  public void testDoesNotInlineDirectObjectReference() {
    testSame("function f(){var x={a:1};return x;}");
  }
}
