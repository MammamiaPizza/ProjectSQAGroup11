package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.TernaryValue;
import org.junit.Test;

public class NodeUtilBooleanValueRegressionTest {

  @Test
  public void testPureBooleanValueForStringLiterals() {
    assertEquals(
        TernaryValue.FALSE,
        NodeUtil.getPureBooleanValue(Node.newString(Token.STRING, "")));
    assertEquals(
        TernaryValue.TRUE,
        NodeUtil.getPureBooleanValue(Node.newString(Token.STRING, "0")));
  }

  @Test
  public void testPureBooleanValueForNumericBoundaries() {
    assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(Node.newNumber(0)));
    assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(-1)));
    assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(Node.newNumber(0.5)));
  }

  @Test
  public void testPureBooleanValueForKnownNamesAndUnknownName() {
    assertEquals(
        TernaryValue.FALSE,
        NodeUtil.getPureBooleanValue(Node.newString(Token.NAME, "undefined")));
    assertEquals(
        TernaryValue.FALSE,
        NodeUtil.getPureBooleanValue(Node.newString(Token.NAME, "NaN")));
    assertEquals(
        TernaryValue.TRUE,
        NodeUtil.getPureBooleanValue(Node.newString(Token.NAME, "Infinity")));
    assertEquals(
        TernaryValue.UNKNOWN,
        NodeUtil.getPureBooleanValue(Node.newString(Token.NAME, "value")));
  }

  @Test
  public void testPureBooleanValueForVoidOfSideEffectFreeExpression() {
    Node voidZero = new Node(Token.VOID, Node.newNumber(0));

    assertEquals(TernaryValue.FALSE, NodeUtil.getPureBooleanValue(voidZero));
  }

  @Test
  public void testPureBooleanValueForVoidOfCallIsUnknown() {
    Node call = new Node(Token.CALL, Node.newString(Token.NAME, "sideEffect"));
    Node voidCall = new Node(Token.VOID, call);

    assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(voidCall));
  }

  @Test
  public void testImpureBooleanValueForVoidOfCallIsUnknown() {
    Node call = new Node(Token.CALL, Node.newString(Token.NAME, "sideEffect"));
    Node voidCall = new Node(Token.VOID, call);

    assertEquals(TernaryValue.FALSE, NodeUtil.getImpureBooleanValue(voidCall));
  }

  @Test
  public void testPureBooleanValueForArrayWithAndWithoutSideEffects() {
    Node emptyArray = new Node(Token.ARRAYLIT);
    Node call = new Node(Token.CALL, Node.newString(Token.NAME, "sideEffect"));
    Node effectfulArray = new Node(Token.ARRAYLIT, call);

    assertEquals(TernaryValue.TRUE, NodeUtil.getPureBooleanValue(emptyArray));
    assertEquals(TernaryValue.UNKNOWN, NodeUtil.getPureBooleanValue(effectfulArray));
  }

  @Test
  public void testImpureBooleanValueUsesLastCommaExpression() {
    Node comma =
        new Node(
            Token.COMMA,
            Node.newString(Token.NAME, "sideEffect"),
            Node.newString(Token.STRING, "result"));

    assertEquals(TernaryValue.TRUE, NodeUtil.getImpureBooleanValue(comma));
  }
}
