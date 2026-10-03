package com.google.javascript.jscomp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class NodeUtilSideEffectsTest {

  private static Node mathCall(String method, Node... arguments) {
    Node target =
        new Node(
            Token.GETPROP,
            Node.newString(Token.NAME, "Math"),
            Node.newString(Token.STRING, method));
    Node call = new Node(Token.CALL, target);
    for (Node argument : arguments) {
      call.addChildToBack(argument);
    }
    return call;
  }

  private static Node call(String name, Node... arguments) {
    Node call = new Node(Token.CALL, Node.newString(Token.NAME, name));
    for (Node argument : arguments) {
      call.addChildToBack(argument);
    }
    return call;
  }

  @Test
  public void testKnownMathCallHasNoSideEffects() {
    Node floorCall = mathCall("floor", Node.newNumber(1));

    assertFalse(NodeUtil.functionCallHasSideEffects(floorCall));
    assertFalse(NodeUtil.mayHaveSideEffects(floorCall));
    assertFalse(NodeUtil.mayEffectMutableState(floorCall));
  }

  @Test
  public void testUnknownFunctionCallHasSideEffects() {
    Node unknownCall = call("externalFunction", Node.newNumber(1));

    assertTrue(NodeUtil.functionCallHasSideEffects(unknownCall));
    assertTrue(NodeUtil.mayHaveSideEffects(unknownCall));
    assertTrue(NodeUtil.mayEffectMutableState(unknownCall));
  }

  @Test
  public void testPureMathCallRetainsSideEffectingArgument() {
    Node floorCall = mathCall("floor", call("sideEffect"));

    assertFalse(NodeUtil.functionCallHasSideEffects(floorCall));
    assertTrue(NodeUtil.mayHaveSideEffects(floorCall));
    assertTrue(NodeUtil.mayEffectMutableState(floorCall));
  }

  @Test
  public void testPureOperatorTreeHasNoSideEffects() {
    Node addition = new Node(Token.ADD, Node.newNumber(1), Node.newNumber(2));

    assertFalse(NodeUtil.mayHaveSideEffects(addition));
    assertFalse(NodeUtil.mayEffectMutableState(addition));
  }

  @Test
  public void testOperatorTreeWithCallOperandHasSideEffects() {
    Node addition = new Node(Token.ADD, Node.newNumber(1), call("sideEffect"));

    assertTrue(NodeUtil.mayHaveSideEffects(addition));
    assertTrue(NodeUtil.mayEffectMutableState(addition));
  }

  @Test
  public void testAssignmentsAndIncrementHaveSideEffects() {
    Node assignment =
        new Node(Token.ASSIGN, Node.newString(Token.NAME, "x"), Node.newNumber(1));
    Node increment = new Node(Token.INC, Node.newString(Token.NAME, "x"));

    assertTrue(NodeUtil.mayHaveSideEffects(assignment));
    assertTrue(NodeUtil.mayEffectMutableState(assignment));
    assertTrue(NodeUtil.mayHaveSideEffects(increment));
    assertTrue(NodeUtil.mayEffectMutableState(increment));
  }
}
