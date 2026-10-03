package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;

public class NodeUtilTest extends TestCase {

  // ---- helper factories for expression nodes ----

  private static Node name(String s) {
    return Node.newString(Token.NAME, s, 0, 0); }

  private static Node number(double d) {
    return Node.newNumber(d); }

  private static Node string(String s) {
    return Node.newString(s); }

  private static Node deleteNode(Node expr) {
    Node n = new Node(Token.DELPROP, expr, 0, 0);
    return n; }

  private static Node voidNode(Node expr) {
    Node n = new Node(Token.VOID, expr, 0, 0);
    return n; }

  private static Node typeofNode(Node expr) {
    Node n = new Node(Token.TYPEOF, expr, 0, 0);
    return n; }

  private static Node eq(Node left, Node right) {
    Node n = new Node(Token.EQ, left, right);
    return n; }

  private static Node neq(Node left, Node right) {
    Node n = new Node(Token.NE, left, right);
    return n; }

  private static Node sheq(Node left, Node right) {
    Node n = new Node(Token.SHEQ, left, right);
    return n; }

  private static Node shne(Node left, Node right) {
    Node n = new Node(Token.SHNE, left, right);
    return n; }

  private static Node lt(Node left, Node right) {
    Node n = new Node(Token.LT, left, right);
    return n; }

  private static Node gt(Node left, Node right) {
    Node n = new Node(Token.GT, left, right);
    return n; }

  private static Node le(Node left, Node right) {
    Node n = new Node(Token.LE, left, right);
    return n; }

  private static Node ge(Node left, Node right) {
    Node n = new Node(Token.GE, left, right);
    return n; }

  private static Node and(Node left, Node right) {
    Node n = new Node(Token.AND, left, right);
    return n; }

  private static Node or(Node left, Node right) {
    Node n = new Node(Token.OR, left, right);
    return n; }

  private static Node not(Node expr) {
    Node n = new Node(Token.NOT, expr);
    return n; }

  private static Node comma(Node left, Node right) {
    Node n = new Node(Token.COMMA, left, right);
    return n; }

  private static Node assign(Node left, Node right) {
    Node n = new Node(Token.ASSIGN, left, right);
    return n; }

  private static Node hook(Node cond, Node trueExpr, Node falseExpr) {
    Node n = new Node(Token.HOOK, cond, trueExpr, falseExpr);
    return n; }

  // ---- isBooleanResult -------------------------------------------------

  public void testIsBooleanResultDelete() {
    Node del = deleteNode(name("x"));
    // delete always returns a boolean
    assertTrue(NodeUtil.isBooleanResult(del)); }

  public void testIsBooleanResultVoid() {
    Node v = voidNode(number(0));
    // void returns undefined, which is not a boolean result
    assertFalse(NodeUtil.isBooleanResult(v)); }

  public void testIsBooleanResultTypeof() {
    Node t = typeofNode(name("x"));
    // typeof returns a string
    assertFalse(NodeUtil.isBooleanResult(t)); }

  public void testIsBooleanResultRelational() {
    // all relational operators always produce a boolean
    assertTrue(NodeUtil.isBooleanResult(eq(number(1), number(2))));
    assertTrue(NodeUtil.isBooleanResult(neq(number(1), number(2))));
    assertTrue(NodeUtil.isBooleanResult(sheq(number(1), number(2))));
    assertTrue(NodeUtil.isBooleanResult(shne(number(1), number(2))));
    assertTrue(NodeUtil.isBooleanResult(lt(number(1), number(2))));
    assertTrue(NodeUtil.isBooleanResult(gt(number(1), number(2))));
    assertTrue(NodeUtil.isBooleanResult(le(number(1), number(2))));
    assertTrue(NodeUtil.isBooleanResult(ge(number(1), number(2)))); }

  public void testIsBooleanResultLogicalAndOr() {
    // && and || do not guarantee a boolean result
    assertFalse(NodeUtil.isBooleanResult(and(number(0), number(1))));
    assertFalse(NodeUtil.isBooleanResult(or(number(0), number(1)))); }

  public void testIsBooleanResultLogicalNot() {
    // ! always returns a boolean
    assertTrue(NodeUtil.isBooleanResult(not(number(0)))); }

  public void testIsBooleanResultLiterals() {
    // boolean literals themselves are boolean results
    assertTrue(NodeUtil.isBooleanResult(new Node(Token.TRUE)));
    assertTrue(NodeUtil.isBooleanResult(new Node(Token.FALSE)));
    // non-boolean literals are not
    assertFalse(NodeUtil.isBooleanResult(number(42)));
    assertFalse(NodeUtil.isBooleanResult(string("hello")));
    assertFalse(NodeUtil.isBooleanResult(new Node(Token.NULL)));
    // undefined literal (NAME "undefined") – not a boolean result
    assertFalse(NodeUtil.isBooleanResult(name("undefined"))); }

  // ---- evaluatesToLocalValue -------------------------------------------

  public void testEvaluatesToLocalValueDelprop() {
    Node del = deleteNode(name("x"));
    // delete has a side effect; it should NOT be considered a purely local value.
    // The primary regression is that it must not throw IllegalStateException.
    assertFalse(NodeUtil.evaluatesToLocalValue(del)); }

  public void testEvaluatesToLocalValueComma() {
    Node c = comma(number(1), number(2));
    // comma with two local values should itself be local
    assertTrue(NodeUtil.evaluatesToLocalValue(c)); }

  public void testEvaluatesToLocalValueAssign() {
    Node a = assign(name("a"), number(1));
    // assignment has side effects, therefore not purely local
    assertFalse(NodeUtil.evaluatesToLocalValue(a)); }

  public void testEvaluatesToLocalValueHook() {
    Node h = hook(number(1), number(2), number(3));
    // ternary expression with all local operands should be local
    assertTrue(NodeUtil.evaluatesToLocalValue(h)); }
}