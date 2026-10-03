package com.google.javascript.jscomp;

import org.junit.Test;

public final class MethodCompilerPassRegressionTest extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new InlineGetters(compiler, false);
  }

  @Test
  public void testObjectLiteralPropertiesDoNotCauseSideEffectingCallToDisappear() {
    testSame("({a:alert,b:alert}).a('a');");
  }

  @Test
  public void testSingleObjectLiteralPropertyCallIsPreserved() {
    testSame("({a:alert}).a('a');");
  }

  @Test
  public void testObjectLiteralPropertiesAreHandledRegardlessOfPropertyOrder() {
    testSame("({b:alert,a:alert}).a('a');");
  }

  @Test
  public void testObjectLiteralGetterInliningCompletesWithoutCompilerError() {
    test(
        "({a:function(){return alert},b:function(){return alert}}).a()('a');",
        "alert('a');");
  }
}