package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.javascript.rhino.Node;
import org.junit.Test;

public class CodeGeneratorInForInitializerTest {

  private String print(String source) {
    Compiler compiler = new Compiler();
    Node root = compiler.parseTestCode(source);
    assertNotNull(root);
    return new CodePrinter.Builder(root).build();
  }

  @Test
  public void testInOperatorInConditionalFalseBranchOfForInitializerIsParenthesized() {
    assertEquals(
        "for(a=c?0:(0 in d);;)foo()",
        print("for(a=c?0:(0 in d);;)foo()"));
  }

  @Test
  public void testInOperatorInNestedConditionalFalseBranchOfForInitializerIsParenthesized() {
    assertEquals(
        "for(a=b?1:c?2:(0 in d);;)foo()",
        print("for(a=b?1:c?2:(0 in d);;)foo()"));
  }

  @Test
  public void testInOperatorInAssignmentInitializerIsParenthesized() {
    assertEquals(
        "for(a=(0 in d);;)foo()",
        print("for(a=(0 in d);;)foo()"));
  }

  @Test
  public void testInOperatorInVarConditionalInitializerIsParenthesized() {
    assertEquals(
        "for(var a=c?0:(0 in d);;)foo()",
        print("for(var a=c?0:(0 in d);;)foo()"));
  }

  @Test
  public void testInOperatorInForConditionDoesNotNeedNoInParentheses() {
    assertEquals(
        "for(;c?0:0 in d;)foo()",
        print("for(;c?0:(0 in d);)foo()"));
  }
}