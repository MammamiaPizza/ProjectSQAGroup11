package com.google.javascript.jscomp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Predicate;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class NodeUtilDefects4JTest {

  private static Node parenthesizedDeleteOfName(String name) {
    Node delete = new Node(Token.DELPROP, Node.newString(Token.NAME, name));
    delete.putBooleanProp(Node.PARENTHESIZED_PROP, true);
    return delete;
  }

  @Test
  public void testDeleteExpressionHasBooleanResult() {
    assertTrue(
        "The JavaScript delete operator always evaluates to a boolean value.",
        NodeUtil.isBooleanResult(parenthesizedDeleteOfName("x")));
  }

  @Test
  public void testComparisonAndNegationHaveBooleanResults() {
    Node comparison =
        new Node(
            Token.EQ,
            Node.newString(Token.NAME, "left"),
            Node.newString(Token.NAME, "right"));
    Node negation = new Node(Token.NOT, Node.newString(Token.NAME, "value"));

    assertTrue(NodeUtil.isBooleanResult(comparison));
    assertTrue(NodeUtil.isBooleanResult(negation));
  }

  @Test
  public void testNonBooleanValueIsNotClassifiedAsBooleanResult() {
    assertFalse(NodeUtil.isBooleanResult(Node.newString(Token.NAME, "value")));
    assertFalse(NodeUtil.isBooleanResult(new Node(Token.NUMBER)));
  }

  @Test
  public void testParenthesizedDeleteEvaluatesToLocalValue() {
    assertTrue(NodeUtil.evaluatesToLocalValue(parenthesizedDeleteOfName("x")));
  }

  @Test
  public void testDeleteEvaluatesToLocalValueRegardlessOfNameLocalityPredicate() {
    Node delete = parenthesizedDeleteOfName("nonLocalName");

    assertTrue(
        NodeUtil.evaluatesToLocalValue(
            delete,
            new Predicate<Node>() {
              @Override
              public boolean apply(Node node) {
                return false;
              }
            }));
  }
}