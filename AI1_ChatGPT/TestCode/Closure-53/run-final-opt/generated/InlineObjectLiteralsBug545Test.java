package com.google.javascript.jscomp;

import com.google.common.base.Supplier;
import org.junit.Test;

public class InlineObjectLiteralsBug545Test extends CompilerTestCase {

  public InlineObjectLiteralsBug545Test() {
    super("");
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

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testBug545SelfReferentialReassignmentDoesNotCrashOrInline() {
    testSame(
        "function f() {"
            + "var x = {a: 1};"
            + "x = {a: x.a};"
            + "return x.a;"
            + "}");
  }

  @Test
  public void testSimpleObjectPropertyIsInlined() {
    test(
        "function f() { var x = {a: 1}; return x.a; }",
        "function f() {"
            + "var JSCompiler_object_inline_a_0 = 1;"
            + "return JSCompiler_object_inline_a_0;"
            + "}");
  }

  @Test
  public void testDirectObjectReferencePreventsInlining() {
    testSame(
        "function f() {"
            + "var x = {a: 1};"
            + "return x;"
            + "}");
  }

  @Test
  public void testMethodCallPropertyReferencePreventsInlining() {
    testSame(
        "function f() {"
            + "var x = {m: function() { return 1; }};"
            + "return x.m();"
            + "}");
  }

  @Test
  public void testGlobalObjectIsNotInlined() {
    testSame("var x = {a: 1}; x.a;");
  }

  @Test
  public void testNestedScopeSelfReferentialAssignmentDoesNotCrash() {
    testSame(
        "function f() {"
            + "var x = {a: 1};"
            + "function g() { x = {a: x.a}; }"
            + "g();"
            + "return x.a;"
            + "}");
  }
}
