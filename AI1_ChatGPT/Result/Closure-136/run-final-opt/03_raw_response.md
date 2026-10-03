package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public final class MethodCompilerPassRegressionTest {

  private void testSame(String source) {
    assertEquals(compile(source, false), compile(source, true));
  }

  private void test(String source, String expected) {
    assertEquals(compile(expected, false), compile(source, true));
  }

  private String compile(String source, boolean runInlineGetters) {
    Compiler compiler = new Compiler();
    compiler.compile(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());

    if (runInlineGetters) {
      Node root = compiler.getRoot();
      Node externs = root.getFirstChild();
      new InlineGetters(compiler, false).process(externs, externs.getNext());
    }

    return compiler.toSource();
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