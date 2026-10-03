package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class PeepholeSubstituteAlternateSyntaxBug759Test {

  private Node optimize(Node expression) {
    Node script = new Node(Token.SCRIPT);
    script.addChildToBack(new Node(Token.EXPR_RESULT, expression));

    Compiler compiler = new Compiler();
    for (int i = 0; i < 2; i++) {
      new PeepholeOptimizationsPass(
          compiler, new PeepholeSubstituteAlternateSyntax(false)).process(null, script);
    }

    return script.getFirstChild().getFirstChild();
  }

  private Node stringCall(Node argument) {
    return new Node(
        Token.CALL,
        Node.newString(Token.NAME, "String"),
        argument);
  }

  private void assertStringConversion(Node result, Node argument) {
    assertEquals(Token.ADD, result.getType());
    assertEquals(2, result.getChildCount());
    assertEquals(Token.STRING, result.getFirstChild().getType());
    assertEquals("", result.getFirstChild().getString());
    assertSame(argument, result.getLastChild());
    assertNull(result.getLastChild().getNext());
  }

  @Test
  public void testFoldsStringWithImmutableLiteralArguments() {
    Node string = Node.newString(Token.STRING, "value");
    assertStringConversion(optimize(stringCall(string)), string);

    Node number = Node.newNumber(0);
    assertStringConversion(optimize(stringCall(number)), number);

    Node bool = new Node(Token.TRUE);
    assertStringConversion(optimize(stringCall(bool)), bool);

    Node nullNode = new Node(Token.NULL);
    assertStringConversion(optimize(stringCall(nullNode)), nullNode);
  }

  @Test
  public void testDoesNotFoldStringWithVariableArgument() {
    Node call = stringCall(Node.newString(Token.NAME, "value"));
    assertSame(call, optimize(call));
  }

  @Test
  public void testDoesNotFoldStringWithCallResultArgument() {
    Node call = stringCall(
        new Node(Token.CALL, Node.newString(Token.NAME, "getValue")));
    assertSame(call, optimize(call));
  }

  @Test
  public void testDoesNotFoldStringWithoutArguments() {
    Node call = new Node(Token.CALL, Node.newString(Token.NAME, "String"));
    assertSame(call, optimize(call));
  }

  @Test
  public void testDoesNotDropAdditionalStringArguments() {
    Node call = stringCall(Node.newString(Token.STRING, "value"));
    call.addChildToBack(
        new Node(Token.CALL, Node.newString(Token.NAME, "sideEffect")));
    assertSame(call, optimize(call));
  }
}
