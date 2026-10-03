package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class CodeGeneratorAssociativityRegressionTest {

  @Test
  public void testPrintPreservesRightNestedOperatorGrouping() {
    Node script = new Node(Token.SCRIPT);

    Node var = new Node(Token.VAR);
    var.addChildToBack(name("a"));
    var.addChildToBack(name("b"));
    var.addChildToBack(name("c"));
    script.addChildToBack(var);

    script.addChildToBack(expr(binary(
        Token.OR, name("a"), binary(Token.OR, name("b"), name("c")))));
    script.addChildToBack(expr(binary(
        Token.MUL, name("a"), binary(Token.MUL, name("b"), name("c")))));
    script.addChildToBack(expr(binary(
        Token.BITOR, name("a"), binary(Token.BITOR, name("b"), name("c")))));

    assertEquals(
        "var a,b,c;a||(b||c);a*(b*c);a|(b|c);",
        new CodePrinter.Builder(script).build());
  }

  @Test
  public void testPrintPreservesMixedModuloAndMultiplyGrouping() {
    Node nestedMultiply = binary(
        Token.MUL,
        binary(Token.MOD, number(4), number(3)),
        number(5));
    Node script = new Node(Token.SCRIPT);
    script.addChildToBack(expr(binary(Token.MUL, number(3), nestedMultiply)));

    assertEquals("3*(4%3*5);", new CodePrinter.Builder(script).build());
  }

  @Test
  public void testPeepholeOptimizationDoesNotDiscardAssociativeGrouping() {
    Node expression = binary(
        Token.OR, name("a"), binary(Token.OR, name("b"), name("c")));

    PeepholeSubstituteAlternateSyntax optimizer =
        new PeepholeSubstituteAlternateSyntax(false);
    Node optimized = optimizer.optimizeSubtree(expression);

    assertSame(expression, optimized);

    Node script = new Node(Token.SCRIPT);
    script.addChildToBack(expr(optimized));
    assertEquals("a||(b||c);", new CodePrinter.Builder(script).build());
  }

  private static Node expr(Node expression) {
    return new Node(Token.EXPR_RESULT, expression);
  }

  private static Node binary(int token, Node left, Node right) {
    return new Node(token, left, right);
  }

  private static Node name(String value) {
    return Node.newString(Token.NAME, value);
  }

  private static Node number(double value) {
    return Node.newNumber(value);
  }
}