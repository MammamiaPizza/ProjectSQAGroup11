package com.google.javascript.jscomp;

 import com.google.common.base.Predicates;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 import junit.framework.TestCase;

 /**
  *
  * Tests for {@link NodeUtil} side-effect detection methods
  * related to Closure bug 61 / PeepholeRemoveDeadCode.
  */
 public class NodeUtilSideEffectTest extends TestCase {

   private Node newMathSinCall() {
     Node math = Node.newString(Token.NAME, "Math", -1, -1);
     Node prop = Node.newString(Token.STRING, "sin", -1, -1);
     Node getprop = new Node(Token.GETPROP, math, prop);
     return new Node(Token.CALL, getprop);
   }

   private Node newStringCall() {
     Node fun = Node.newString(Token.NAME, "String", -1, -1);
     Node call = new Node(Token.CALL, fun, Node.newNumber(42.0));
     return call;
   }

   // ------------------------------------------------
   // mayHaveSideEffects
   // ------------------------------------------------
   public void testVoidHasNoSideEffects() {
     Node v = new Node(Token.VOID, Node.newNumber(0.0));
     assertFalse(NodeUtil.mayHaveSideEffects(v));
   }

   public void testTypeofHasNoSideEffects() {
     Node t = new Node(Token.TYPEOF,
             Node.newString(Token.NAME, "x", -1, -1));
     assertFalse(NodeUtil.mayHaveSideEffects(t));
   }

   public void testCommaWithLiteralsHasNoSideEffects() {
     Node left = Node.newString("a");
     Node right = Node.newNumber(1.0);
     Node comma = new Node(Token.COMMA, left, right);
     assertFalse(NodeUtil.mayHaveSideEffects(comma));
   }

   public void testCommaWithAssignHasSideEffects() {
     Node left = Node.newString("a");
     Node assign = new Node(Token.ASSIGN,
             Node.newString(Token.NAME, "x", -1, -1),
             Node.newNumber(1.0));
     Node comma = new Node(Token.COMMA, left, assign);
     assertTrue(NodeUtil.mayHaveSideEffects(comma));
   }

   public void testPureCallNoSideEffects() {
     assertFalse(NodeUtil.mayHaveSideEffects(newMathSinCall()));
     assertFalse(NodeUtil.mayHaveSideEffects(newStringCall()));
   }

   public void testAssignmentHasSideEffects() {
     Node assign = new Node(Token.ASSIGN,
             Node.newString(Token.NAME, "a", -1, -1),
             Node.newNumber(1.0));
     assertTrue(NodeUtil.mayHaveSideEffects(assign));
   }

   public void testNewObjectHasSideEffects() {
     Node n = new Node(Token.NEW,
             Node.newString(Token.NAME, "Object", -1, -1));
     assertFalse(NodeUtil.mayHaveSideEffects(n));
   }

   public void testIncDecHasSideEffects() {
     Node inc = new Node(Token.INC,
             Node.newString(Token.NAME, "i", -1, -1));
     assertTrue(NodeUtil.mayHaveSideEffects(inc));
   }

   public void testDeleteHasSideEffects() {
     Node del = new Node(Token.DELPROP,
             Node.newString(Token.NAME, "x", -1, -1));
     assertTrue(NodeUtil.mayHaveSideEffects(del));
   }

   public void testThrowHasSideEffects() {
     Node t = new Node(Token.THROW,
             Node.newString("error"));
     assertTrue(NodeUtil.mayHaveSideEffects(t));
   }

   public void testImmutableValuesNoSideEffects() {
     assertFalse(NodeUtil.mayHaveSideEffects(Node.newString("s")));
     assertFalse(NodeUtil.mayHaveSideEffects(Node.newNumber(42.0)));
     assertFalse(NodeUtil.mayHaveSideEffects(new Node(Token.TRUE)));
     assertFalse(NodeUtil.mayHaveSideEffects(new Node(Token.FALSE)));
     assertFalse(NodeUtil.mayHaveSideEffects(new Node(Token.NULL)));
     assertFalse(NodeUtil.mayHaveSideEffects(
             Node.newString(Token.NAME, "undefined", -1, -1)));
     assertFalse(NodeUtil.mayHaveSideEffects(
             Node.newString(Token.NAME, "Infinity", -1, -1)));
     assertFalse(NodeUtil.mayHaveSideEffects(
             Node.newString(Token.NAME, "NaN", -1, -1)));
   }

   // ------------------------------------------------
   // callHasLocalResult / evaluatesToLocalValue
   // ------------------------------------------------
   public void testPureCallsHaveLocalResult() {
     assertFalse(NodeUtil.callHasLocalResult(newMathSinCall()));
     assertFalse(NodeUtil.callHasLocalResult(newStringCall()));
   }

   public void testLiteralsEvaluateToLocalValue() {
     assertTrue(NodeUtil.evaluatesToLocalValue(Node.newNumber(1.0)));
     assertTrue(NodeUtil.evaluatesToLocalValue(Node.newString("x")));
     assertTrue(NodeUtil.evaluatesToLocalValue(new Node(Token.TRUE)));
   }

 }