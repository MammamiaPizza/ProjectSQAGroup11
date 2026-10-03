package com.google.javascript.jscomp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class NodeUtilConditionalExpressionTest {

  private static Node name(String value) {
    return Node.newString(Token.NAME, value);
  }

  private static Node node(int type, Node... children) {
    Node result = new Node(type);
    for (Node child : children) {
      result.addChildToBack(child);
    }
    return result;
  }

  @Test
  public void testOrOfFunctionReferencesHasNoSideEffects() {
    Node expression = node(Token.OR, name("f"), name("g"));

    assertFalse(NodeUtil.mayHaveSideEffects(expression));
    assertFalse(NodeUtil.mayEffectMutableState(expression));
  }

  @Test
  public void testHookOfFunctionReferencesHasNoSideEffects() {
    Node expression = node(Token.HOOK, name("condition"), name("g"), name("h"));

    assertFalse(NodeUtil.mayHaveSideEffects(expression));
    assertFalse(NodeUtil.mayEffectMutableState(expression));
  }

  @Test
  public void testOrCallTargetEvaluationHasNoSideEffectsButCallDoes() {
    Node callTarget = node(Token.OR, name("f"), name("g"));
    Node call = node(Token.CALL, callTarget);

    assertFalse(NodeUtil.mayHaveSideEffects(callTarget));
    assertTrue(NodeUtil.mayHaveSideEffects(call));
  }

  @Test
  public void testHookCallTargetEvaluationHasNoSideEffectsButCallDoes() {
    Node callTarget = node(Token.HOOK, name("condition"), name("g"), name("h"));
    Node call = node(Token.CALL, callTarget);

    assertFalse(NodeUtil.mayHaveSideEffects(callTarget));
    assertTrue(NodeUtil.mayHaveSideEffects(call));
  }

  @Test
  public void testOrPropagatesSideEffectsFromEitherAlternative() {
    Node assignment = node(Token.ASSIGN, name("x"), Node.newNumber(1));
    Node expression = node(Token.OR, name("f"), assignment);

    assertTrue(NodeUtil.mayHaveSideEffects(expression));
    assertTrue(NodeUtil.mayEffectMutableState(expression));
  }

  @Test
  public void testHookPropagatesSideEffectsFromConditionAndBranches() {
    Node conditionCall = node(Token.CALL, name("check"));
    Node expression = node(Token.HOOK, conditionCall, name("g"), name("h"));

    assertTrue(NodeUtil.mayHaveSideEffects(expression));
    assertTrue(NodeUtil.mayEffectMutableState(expression));
  }

@org.junit.Test
public void testCanBeSideEffectedHonorsKnownConstants() {
  com.google.javascript.rhino.Node name =
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.NAME, "value");

  junit.framework.Assert.assertTrue(NodeUtil.canBeSideEffected(name));

  java.util.Set<String> knownConstants = new java.util.HashSet<String>();
  knownConstants.add("value");
  junit.framework.Assert.assertFalse(
      NodeUtil.canBeSideEffected(name, knownConstants));
}

@org.junit.Test
public void testCanBeSideEffectedRecognizesCallsPropertiesAndChildren() {
  junit.framework.Assert.assertTrue(
      NodeUtil.canBeSideEffected(
          new com.google.javascript.rhino.Node(
              com.google.javascript.rhino.Token.CALL)));
  junit.framework.Assert.assertTrue(
      NodeUtil.canBeSideEffected(
          new com.google.javascript.rhino.Node(
              com.google.javascript.rhino.Token.GETPROP)));

  com.google.javascript.rhino.Node expression =
      new com.google.javascript.rhino.Node(com.google.javascript.rhino.Token.OR);
  expression.addChildToBack(
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.STRING, "literal"));
  expression.addChildToBack(
      com.google.javascript.rhino.Node.newString(
          com.google.javascript.rhino.Token.NAME, "mutable"));

  junit.framework.Assert.assertTrue(NodeUtil.canBeSideEffected(expression));
}
}
