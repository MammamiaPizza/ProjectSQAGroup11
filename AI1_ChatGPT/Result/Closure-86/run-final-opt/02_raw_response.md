package com.google.javascript.jscomp;

import com.google.common.base.Predicate;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;

public class NodeUtilLocalValueRegressionTest extends TestCase {

  private static Node name(String value) {
    return Node.newString(Token.NAME, value);
  }

  private static Node newExpression(String constructorName) {
    return new Node(Token.NEW, name(constructorName));
  }

  private static final Predicate<Node> ONLY_LOCAL = new Predicate<Node>() {
    @Override
    public boolean apply(Node node) {
      return node.isName() && "local".equals(node.getString());
    }
  };

  private static final Predicate<Node> NO_LOCALS = new Predicate<Node>() {
    @Override
    public boolean apply(Node node) {
      return false;
    }
  };

  public void testNewExpressionAlwaysEvaluatesToLocalValue() {
    assertTrue(NodeUtil.evaluatesToLocalValue(newExpression("Constructor"), NO_LOCALS));
  }

  public void testNewStringEvaluatesToLocalValue() {
    assertTrue(NodeUtil.evaluatesToLocalValue(newExpression("String"), NO_LOCALS));
  }

  public void testNewSetLocationEvaluatesToLocalValue() {
    assertTrue(NodeUtil.evaluatesToLocalValue(newExpression("setLocation"), NO_LOCALS));
  }

  public void testNewExpressionWithArgumentsStillEvaluatesToLocalValue() {
    Node constructor = newExpression("Constructor");
    constructor.addChildToBack(new Node(Token.CALL, name("unknownFunction")));

    assertTrue(NodeUtil.evaluatesToLocalValue(constructor, NO_LOCALS));
  }

  public void testObjectAndArrayLiteralsEvaluateToLocalValue() {
    assertTrue(NodeUtil.evaluatesToLocalValue(new Node(Token.OBJECTLIT), NO_LOCALS));
    assertTrue(NodeUtil.evaluatesToLocalValue(new Node(Token.ARRAYLIT), NO_LOCALS));
  }

  public void testNameLocalityIsControlledByPredicate() {
    assertTrue(NodeUtil.evaluatesToLocalValue(name("local"), ONLY_LOCAL));
    assertFalse(NodeUtil.evaluatesToLocalValue(name("external"), ONLY_LOCAL));
  }

  public void testPropertyAccessDoesNotAssumeLocalResult() {
    Node propertyAccess =
        new Node(
            Token.GETPROP,
            name("local"),
            Node.newString(Token.STRING, "possiblyAliasedProperty"));

    assertFalse(NodeUtil.evaluatesToLocalValue(propertyAccess, ONLY_LOCAL));
  }

  public void testOrdinaryCallDoesNotAssumeLocalResult() {
    Node call = new Node(Token.CALL, name("unknownFunction"));

    assertFalse(NodeUtil.evaluatesToLocalValue(call, ONLY_LOCAL));
  }

  public void testAssignmentResultUsesLocalRightHandValue() {
    Node assignment = new Node(Token.ASSIGN, name("target"), new Node(Token.OBJECTLIT));

    assertTrue(NodeUtil.evaluatesToLocalValue(assignment, NO_LOCALS));
  }
}