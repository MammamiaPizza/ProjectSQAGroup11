package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import junit.framework.TestCase;

/**

 - Tests for the bug 141 fix in {@link NodeUtil#mayHaveSideEffects(Node)}
 - and {@link PureFunctionIdentifier}.  The bug caused pure HOOK/OR expressions
 - to be incorrectly treated as having side effects, preventing pure-function
 - detection and expression decomposition.
  */
 public class Bug141Test extends TestCase {

  // ---------- helpers ----------

  private static Node name(String id) {
    return Node.newString(Token.NAME, id); }

  private static Node call(String fnName) {
    return new Node(Token.CALL, name(fnName)); }

  private static Node or(Node left, Node right) {
    return new Node(Token.OR, left, right); }

  private static Node hook(Node cond, Node trueExpr, Node falseExpr) {
    return new Node(Token.HOOK, cond, trueExpr, falseExpr); }

  // ---------- mayHaveSideEffects – HOOK ----------

  public void testHookAllPureChildren_noSideEffects() {
    Node cond = name("a");
    Node t = name("b");
    Node f = name("c");
    Node h = hook(cond, t, f);
    assertFalse(NodeUtil.mayHaveSideEffects(h)); }

  public void testHookConditionHasSideEffects() {
    Node cond = call("sideEffectFn");
    Node t = name("b");
    Node f = name("c");
    Node h = hook(cond, t, f);
    assertTrue(NodeUtil.mayHaveSideEffects(h)); }

  public void testHookTrueBranchHasSideEffects() {
    Node cond = name("a");
    Node t = call("sideEffectFn");
    Node f = name("c");
    Node h = hook(cond, t, f);
    assertTrue(NodeUtil.mayHaveSideEffects(h)); }

  public void testHookWithAssignmentInBranch() {
    Node cond = name("a");
    Node assign = new Node(Token.ASSIGN, name("x"), Node.newNumber(1));
    Node f = name("c");
    Node h = hook(cond, assign, f);
    assertTrue(NodeUtil.mayHaveSideEffects(h)); }

  public void testNestedHookPure_noSideEffects() {
    // a ? (c ? d : e) : f   all pure
    Node inner = hook(name("c"), name("d"), name("e"));
    Node h = hook(name("a"), inner, name("f"));
    assertFalse(NodeUtil.mayHaveSideEffects(h)); }

  // ---------- mayHaveSideEffects – OR ----------

  public void testOrAllPureChildren_noSideEffects() {
    Node left = name("x");
    Node right = name("y");
    Node o = or(left, right);
    assertFalse(NodeUtil.mayHaveSideEffects(o)); }

  public void testOrLeftHasSideEffects() {
    Node left = call("sideEffectFn");
    Node right = name("y");
    assertTrue(NodeUtil.mayHaveSideEffects(or(left, right))); }

  public void testOrRightHasSideEffects() {
    Node left = name("x");
    Node right = call("sideEffectFn");
    assertTrue(NodeUtil.mayHaveSideEffects(or(left, right))); }

  public void testNestedOrPure_noSideEffects() {
    // (x || y) || z    all pure
    Node inner = or(name("x"), name("y"));
    Node o = or(inner, name("z"));
    assertFalse(NodeUtil.mayHaveSideEffects(o)); }

  // ---------- other node types ----------

  public void testSimpleCall_hasSideEffects() {
    assertTrue(NodeUtil.mayHaveSideEffects(call("f"))); }

  // ---------- PureFunctionIdentifier integration ----------

  /**

 - A call like {@code (f || g)(1)} should be detected as a call to a pure
 - function when both {@code f} and {@code g} are pure.  The bug caused this
 - call to be entirely invisible to the pure-function detection.
    */
   public void testPureFunctionIdentifier_OrCallDetected() throws Exception {
 String source = "function f(){} function g(){} function test(){(f||g)(1);}";
 Compiler compiler = new Compiler();
 compiler.initOptions(new CompilerOptions());
 // parse source as the main script; externs remain empty
 Node externs = new Node(Token.SCRIPT);
 Node src = compiler.parse("<test>", source);
 assertNotNull(src);

 SimpleDefinitionFinder defFinder = new SimpleDefinitionFinder(compiler);
 PureFunctionIdentifier pass = new PureFunctionIdentifier(compiler, defFinder);
 pass.process(externs, src);

 String report = pass.getDebugReport();
 // The function "test" must appear in the pure-functions list.
 assertTrue("Expected pure function 'test' in report: " + report,
            report.contains("test")); }

  /**

 - When one operand of an OR has side effects the containing function
 - should NOT be marked pure.
    */
   public void testPureFunctionIdentifier_OrCallWithSideEffect_notPure() throws Exception {
 String source =
    "function f(){}" +
    "function bad(){ var x = 1; return x; }" +  // not pure – has assignment
    "function test(){(f||bad)(1);}";
 Compiler compiler = new Compiler();
 compiler.initOptions(new CompilerOptions());
 Node externs = new Node(Token.SCRIPT);
 Node src = compiler.parse("<test>", source);
 assertNotNull(src);

 SimpleDefinitionFinder defFinder = new SimpleDefinitionFinder(compiler);
 PureFunctionIdentifier pass = new PureFunctionIdentifier(compiler, defFinder);
 pass.process(externs, src);

 String report = pass.getDebugReport();
 assertFalse("Function 'test' should NOT be pure when operand has side effects",
             report.contains("test")); }

}