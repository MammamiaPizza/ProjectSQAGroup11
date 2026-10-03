package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class InlineCostEstimatorGeneratedTest {

  @Test
  public void testBooleanAndNullConstantsHaveNoEstimatedCost() {
    assertEquals(0, InlineCostEstimator.getCost(new Node(Token.TRUE)));
    assertEquals(0, InlineCostEstimator.getCost(new Node(Token.FALSE)));
    assertEquals(0, InlineCostEstimator.getCost(new Node(Token.NULL)));
  }

  @Test
  public void testConstantStillAllowsSurroundingOperatorToBeCounted() {
    Node notTrue = new Node(Token.NOT, new Node(Token.TRUE));

    assertEquals(1, InlineCostEstimator.getCost(notTrue));
  }

  @Test
  public void testIdentifierUsesEstimatedIdentifierCost() {
    Node identifier = Node.newString(Token.NAME, "aVeryLongIdentifier");

    assertEquals(
        InlineCostEstimator.ESTIMATED_IDENTIFIER_COST,
        InlineCostEstimator.getCost(identifier));
  }

  @Test
  public void testNumericExpressionCostCountsGeneratedTokens() {
    Node addition = new Node(Token.ADD, Node.newNumber(1), Node.newNumber(2));

    assertEquals(3, InlineCostEstimator.getCost(addition));
  }

  @Test
  public void testThresholdStopsAtBoundaryForTokenByTokenExpression() {
    Node addition = new Node(Token.ADD, Node.newNumber(1), Node.newNumber(2));

    assertEquals(1, InlineCostEstimator.getCost(addition, 1));
    assertEquals(2, InlineCostEstimator.getCost(addition, 2));
    assertEquals(3, InlineCostEstimator.getCost(addition, 3));
  }
}
