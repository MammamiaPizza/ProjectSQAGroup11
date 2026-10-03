package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class ClosureCodingConventionBug57Test {

  private final ClosureCodingConvention convention = new ClosureCodingConvention();

  @Test
  public void testRequireWithStringLiteralExtractsClassName() {
    Node call = createGoogCall("require", Node.newString(Token.STRING, "foo.bar"));
    Node parent = new Node(Token.EXPR_RESULT, call);

    assertEquals("foo.bar", convention.extractClassNameIfRequire(call, parent));
  }

  @Test
  public void testRequireWithNameArgumentDoesNotExtractClassName() {
    Node call = createGoogCall("require", Node.newString(Token.NAME, "foo"));
    Node parent = new Node(Token.EXPR_RESULT, call);

    assertNull(convention.extractClassNameIfRequire(call, parent));
  }

  @Test
  public void testRequireWithNumberArgumentDoesNotExtractClassName() {
    Node call = createGoogCall("require", Node.newNumber(1));
    Node parent = new Node(Token.EXPR_RESULT, call);

    assertNull(convention.extractClassNameIfRequire(call, parent));
  }

  @Test
  public void testRequireOutsideExpressionStatementDoesNotExtractClassName() {
    Node call = createGoogCall("require", Node.newString(Token.STRING, "foo"));
    Node variable = Node.newString(Token.NAME, "required");
    variable.addChildToBack(call);
    Node varStatement = new Node(Token.VAR, variable);

    assertNull(convention.extractClassNameIfRequire(call, variable));
  }

  @Test
  public void testDifferentGoogCallDoesNotExtractRequireClassName() {
    Node call = createGoogCall("provide", Node.newString(Token.STRING, "foo"));
    Node parent = new Node(Token.EXPR_RESULT, call);

    assertNull(convention.extractClassNameIfRequire(call, parent));
  }

  @Test
  public void testRequireWithoutArgumentDoesNotExtractClassName() {
    Node call = createGoogCall("require");
    Node parent = new Node(Token.EXPR_RESULT, call);

    assertNull(convention.extractClassNameIfRequire(call, parent));
  }

  private static Node createGoogCall(String method, Node... arguments) {
    Node callee =
        new Node(
            Token.GETPROP,
            Node.newString(Token.NAME, "goog"),
            Node.newString(Token.STRING, method));
    Node call = new Node(Token.CALL, callee);
    for (Node argument : arguments) {
      call.addChildToBack(argument);
    }
    return call;
  }
}
