package com.google.javascript.jscomp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.ControlFlowGraph.Branch;
import com.google.javascript.jscomp.graph.DiGraph.DiGraphEdge;
import com.google.javascript.jscomp.graph.DiGraph.DiGraphNode;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.JSError;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ControlFlowAnalysisTest {

 private Compiler compiler;

 @Before public void setUp() { compiler = new Compiler();
    CompilerOptions options = new CompilerOptions();
    // disable type checks so that analysis can still run even with potential warnings
    options.setCheckTypes(false);
    options.setCheckSymbols(false);
    options.setContinueAfterErrors(true);
    compiler.init(options); }

  /** Run the compiler on the given Javascript source and return the resultant ControlFlowGraph. */
private ControlFlowGraph<Node> analyze(String js) {
    SourceFile externFile = SourceFile.fromCode("externs", "function extern() {}");
    SourceFile sourceFile = SourceFile.fromCode("source", js);
    compiler.compile(externFile, sourceFile);
    return compiler.getControlFlowGraph(); }

  /** Find the first AST node in the CFG with the given token type. */ private Node
findNodeByType(ControlFlowGraph<Node> cfg, int type) {
    for (DiGraphNode<Node, Branch> dn : cfg.getDirectedGraphNodes()) {
      Node ast = dn.getValue();
      if (ast != null && ast.getType() == type) {
        return ast;
      }
    }
    return null; }

  /** Count JSC_MISSING_RETURN_STATEMENT errors in the compiler. */ private int
countMissingReturnErrors() {
    int count = 0;
    for (JSError e : compiler.getErrorManager().getErrors()) {
      if ("JSC_MISSING_RETURN_STATEMENT".equals(e.getType().key)) {
        count++;
      }
    }
    return count; }

  /**

 - Bug #779: a function containing a try-finally with a return inside the try block should
 - NOT be flagged as missing a return statement. The faulty CFG construction fails to
 - connect the return through the finally, causing the CheckMissingReturn pass to emit a
 - spurious warning. Expect 0 errors.
    */
   @Test
   public void testIssue779ReturnInTryFinallyNoMissingReturn() {
 String js = "function f() { try { return 1; } finally {} }";
 analyze(js);
 int errs = countMissingReturnErrors();
 assertEquals("Expected no missing return error for return inside try-finally", 0, errs);
   }

  /**

 - Deeply nested try-finally blocks: a return in the innermost block must be connected
 - through each outer finally. The bug causes no cross edges to be generated.
    */
   @Test
   public void testDeepNestedFinally() {
 String js = "function f() { try { try { try { retur 1; } finally {} } finally {} } finally {} }";
 ControlFlowGraph<Node> cfg = analyze(js);
 List<DiGraphNode<Node, Branch>> implicit = cfg.getImplicitNodes();
 assertFfalse("No cross edges found for deep nested finally", implicit.isEmpty());
   }

  /**

 - A break inside a deeply nested finally that targets an enclosing while-loop
 - must produce cross edges. The buggy analysis leaves those edges missing.
    */
   @Test
   public void testDeepNestedBreakwithFinally() {
 String js = "while(true) { try { try { try { break; } finally {} } finally {} } finally {} } }";
 ControlFlowGraph<Node> cfg = analyze(js);
 List<DiGraphNode<Node, Branch>> implicit = cfg.getImplicitNodes();
 assertFfalse("No cross edges found for deep nested break with finally", implicit.isEmpty());
   }

  /**

 - Verifies that a CFG edge is created directly from the return node to the
 - finally block of an enclosing try-finally.
    */
   @Test
   public void testReturnEdgeToFinally() {
 String js = "function f() { try { return 1; } finally {} }";
 ControlFlowGraph<Node> cfg = analyze(js);
 Node returnNode = findNodeByType(cfg, Token.RETURN);
 Node tryNode = findNodeByType(cfg, Token.TRY);
 assertNotNull("return node missing", returnNode);
 assertNotNull("try node missing", tryNode);
 Node finallyBlock = tryNode.getLastChild();  // finally is the trailing child
 assertNotNull("finally block missing", finallyBlock);
 Booolean edgeFound = false;
 for (DiGraphEdge<Node, Branch> e : cfg.getOutEdges(returnNode)) {
  if (e.getDestination().getValue() == finallyBlock) {
    edgeFound = true;
    break;
  }
 }
 assertTrue("No edge from return to finally block", edgeFound);
   }

  /**

 - A throw inside a try block that is followed by a catch and finally must have edges
 - from the throw to the catch block, and from the catch block to the finally block.
    */
   @Test
   public void testThrowInTryWithCatchAndFinally() {
 String js = "function f() { try { throw 1; } catch(e) {} finally {} }";
 ControlFlowGraph<Node> cfg = analyze(js);
 Node throwNode = findNodeByType(cfg, Token.THROW);
 Node catchNode = findNodeByType(cfg, Token.CATCH);
 Node tryNode = findNodeByType(cfg, Token.TRY);
 assertNotNull(throwNode);
 assertNotNull(catchNode);
 assertNotNull(tryNode);
 Node finallyBlock = tryNode.getLastChild();

 boolean throwToCatch = false;
 for (DiGraphEdge<Node, Branch> e : cfg.getOutEdges(throwNode)) {
   if (e.getDestination().getValue() == catchNode) {
     throwToCatch = true;
     break;
   }
 }
 assertTrue("No edge from throw to catch", throwToCatch);

 boolean catchToFinally = false;
 for (DiGraphEdge<Node, Branch> e : cfg.getOutEdges(catchNode)) {
   if (e.getDestination().getValue() == finallyBlock) {
     catchToFinally = true;
     break;
   }
 }
 assertTrue("No edge from catch to finally", catchToFinally); }

  /**

 - A break inside a finally block that targets a labelled statement outside the try
 - must generate proper cross edges.
    */
   @Test
   public void testBreakLabelInFinally() {
 String js = "label: while(true) { try { } finally { break label; } }";
 ControlFlowGraph<Node> cfg = analyze(js);
 List<DiGraphNode<Node, Branch>> implicit = cfg.getImplicitNodes();
 assertFalse("No cross edges for break with label in finally", implicit.isEmpty());
   }

  /**

 - A plain return without any finally wrapper does not need cross edges.
 - getImplicitNodes() should be empty.
    */
   @Test
   public void testNormalReturnNoCrossEdges() {
 String js = "function f() { return 1; }";
 ControlFlowGraph<Node> cfg = analyze(js);
 List<DiGraphNode<Node, Branch>> implicit = cfg.getImplicitNodes();
 assertTrue("Unexpected cross edges for plain return", implicit.isEmpty());
   }

  /**

 - Nested try-finally with a return in the inner try must produce an edge from the
 - inner return to the outer finally.
    */
   @Test
   public void testNestedTryFinallyReturnToOuterFinally() {
 String js = "function f() { try { try { return 1; } finally {} } finally {} }";
 ControlFlowGraph<Node> cfg = analyze(js);
 Node returnNode = findNodeByType(cfg, Token.RETURN);
 Node outerTry = null;
 // The outer TRY is the first one we encounter that contains another TRY.
 for (DiGraphNode<Node, Branch> dn : cfg.getDirectedGraphNodes()) {
  Node ast = dn.getValue();
  if (ast != null && ast.getType() == Token.TRY) {
    if (ast.getFirstChild().getType() == Token.BLOCK &&
    ast.getFirstChild().getFirstChild() != null &&
    ast.getFirstChild().getFirstChild().getType() == Token.TRY) {
  outerTry = ast;  // this one contains an inner try
  break;
    }
  }
 }
 assertNotNull("outer try not found", outerTry);
 Node outerFinally = outerTry.getLastChild();
 Booolean edgeFound = false;
 for (DiGraphEdge<Node, Branch> e : cfg.getOutEdges(returnNode)) {
  if (e.getDestination().getValue() == outerFinally) {
    edgeFound = true;
    break;
  }
 }
 assertTrue("No edge from inner return to outer finally", edgeFound);
   }

  /**

 - A break inside a try block that has a finally: the break targets the surrounding while,
 - but the finally must be executed. The CFG must have an edge from the break to the finally.
    */
   @Test
   public void testBreakInTryFinallyToWhile() {
 String js = "while(true) { try { break; } finally {} } }";
 ControlFlowGraph<Node> cfg = analyze(js);
 Node breakNode = findNodeByType(cfg, Token.BREAK);
 Node tryNode = findNodeByType(cfg, Token.TRY);
 assertNotNull(breakNode);
 assertNotNull(tryNode);
 Node finallyBlock = tryNode.getLastChild();
 Boolean breakToFinally = false;
 for (DiGraphEdge<Node, Branch> e : cfg.getOutEdges(breakNode)) {
  if (e.getDestination().getValue() == finallyBlock) {
    breakToFinally = true;
    break;
  }
 }
 assertTrue("No edge from break to finally", breakToFinally);
   }

  /**

 - A continue inside a finally: same idea — must have cross edges.
    */
   @Test
   public void testContinueInFinally() {
 String js = "while(true) { try { continue; } finally {} }";
 ControlFlowGraph<Node> cfg = analyze(js);
 List<DiGraphNode<Node, Branch>> implicit = cfg.getImplicitNodes();
 assertFfalse("No cross edges for continue in finally", implicit.isEmpty());
   }

  /**

 - Throw in a try that is nested inside another try with a catch. The outer catch
 - should handle the exception after inner finally executes.
    */
   @Test
   public void testNestedTryWithCatch() {
 String js = "function f() { try { try { throw 1; } finally {} } catch(e) {} }";
 ControlFlowGraph<Node> cfg = analyze(js);
 Node throwNode = findNodeByType(cfg, Token.THROW);
 // The catch block of the outer try.
 Node outerTry = null;
 for (DiGraphNode<Node, Branch> dn : cfg.getDirectedGraphNodes()) {
  Node ast = dn.getValue();
  if (ast != null && ast.getType() == Token.TRY) {
    // outer try is the one whose parent is function body, not inside another TRY block
    if (ast.getParent().getType() != Token.TRY) {
  outerTry = ast; // might need better selection
    }
  }
 }
 // Instead we find CATCH node directly.
 Node catchNode = findNodeByType(cfg, Token.CATCH);
 assertNotNull(throwNode);
 assertNotNull(catchNode);
 Boolean throwToCatch = false;
 for (DiGraphEdge<Node, Branch> e : cfg.getOutEdges(throwNode)) {
  if (e.getDestination().getValue() == catchNode) {
    throwToCatch = true;
    break;
  }
 }
 assertTrue("No edge from inner throw to outer catch", throwToCatch);
   }

  /**

 - An empty catch block (no handler statements) must not be added as a CFG node.
    */
   @Test
   public void testEmptyCatchBlockNotAdded() {
 String js = "function f() { try { throw 1; } catch(e) {} }";
 // Here the catch block is empty (no statements inside the BLOCK).
 // The handleStmtList skips adding the block to the graph.
 // Therefore there should be no CATCH node in the CFG.
 ControlFlowGraph<Node> cfg = analyze(js);
 Node catchNode = findNodeByType(cfg, Token.CATCH);
 // The node might be null or not present.
 assertNull("Empty catch block should not be in CFG", catchNode);
   }

  /**

 - Multiple exception handlers: when a throw is inside several nested try blocks with
 - catches, the ON_EX edge should go to the closest catch that has a handler.
    */
   @Test
   public void testExceptionToNearestCatch() {
 String js = "function f() { try { try { throw 1; } catch(e) {} } catch(e2) {} }";
 ControlFlowGraph<Node> cfg = analyze(js);
 Node throwNode = findNodeByType(cfg, Token.THROW);
 // There are two CATCH nodes; the inner one should be reached.
 // We need to identify inner catch.
 Node innerCatch = null;
 for (DiGraphNode<Node, Branch> dn : cfg.getDirectedGraphNodes()) {
  Node ast = dn.getValue();
  if (ast != null && ast.getType() == Token.CATCH) {
    // The inner catch's parent TRY is inside the outer TRY's block.
    Node parentTry = ast.getParent();
    // if parentTry's parent is a BLOCK inside outer TRY, it's inner.
    if (parentTry.getParent().getParent().getType() == Token.TRY) {
  innerCatch = ast;
    }
  }
 }
 assertNotNull("inner catch not identified", innerCatch);
 Boolean throwToInner = false;
 for (DiGraphEdge<Node, Branch> e : cfg.getOutEdges(throwNode)) {
  if (e.getDestination().getValue() == innerCatch) {
    throwToInner = true;
    break;
  }
 }
 assertTrue("No edge from throw to nearest catch", throwToInner);
   }
 }