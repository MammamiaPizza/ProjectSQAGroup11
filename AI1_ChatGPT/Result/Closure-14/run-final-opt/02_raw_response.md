package com.google.javascript.jscomp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.jscomp.ControlFlowGraph.Branch;
import com.google.javascript.jscomp.graph.DiGraph.DiGraphEdge;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class ControlFlowAnalysisDefectsTest {

  @Test
  public void testReturnThroughNestedFinallyReachesEveryFinally() {
    Node returnNode = new Node(Token.RETURN);
    Node innerFinally = blockWithExpression("innerCleanup");
    Node outerFinally = blockWithExpression("outerCleanup");

    Node innerTry = tryFinally(blockWith(returnNode), innerFinally);
    Node outerTry = tryFinally(blockWith(innerTry), outerFinally);
    Node script = scriptWith(function("f", blockWith(outerTry)));

    ControlFlowGraph<Node> cfg = analyze(script);

    assertTrue("A return must first enter the innermost finally block.",
        hasEdgeTo(cfg, returnNode, innerFinally));
    assertTrue("The return completion must continue through the outer finally block.",
        isReachable(cfg, returnNode, outerFinally));
  }

  @Test
  public void testReturnThroughThreeNestedFinallyBlocksReachesOuterFinally() {
    Node returnNode = new Node(Token.RETURN);
    Node firstFinally = blockWithExpression("firstCleanup");
    Node secondFinally = blockWithExpression("secondCleanup");
    Node thirdFinally = blockWithExpression("thirdCleanup");

    Node firstTry = tryFinally(blockWith(returnNode), firstFinally);
    Node secondTry = tryFinally(blockWith(firstTry), secondFinally);
    Node thirdTry = tryFinally(blockWith(secondTry), thirdFinally);
    Node script = scriptWith(function("f", blockWith(thirdTry)));

    ControlFlowGraph<Node> cfg = analyze(script);

    assertTrue(hasEdgeTo(cfg, returnNode, firstFinally));
    assertTrue(isReachable(cfg, returnNode, secondFinally));
    assertTrue("Deeply nested finally routing must not lose the outer completion.",
        isReachable(cfg, returnNode, thirdFinally));
  }

  @Test
  public void testBreakThroughNestedFinallyReachesEveryFinally() {
    Node breakNode = new Node(Token.BREAK);
    Node innerFinally = blockWithExpression("innerCleanup");
    Node outerFinally = blockWithExpression("outerCleanup");

    Node innerTry = tryFinally(blockWith(breakNode), innerFinally);
    Node outerTry = tryFinally(blockWith(innerTry), outerFinally);
    Node whileNode = new Node(Token.WHILE, new Node(Token.TRUE), blockWith(outerTry));
    Node script = scriptWith(function("f", blockWith(whileNode)));

    ControlFlowGraph<Node> cfg = analyze(script);

    assertTrue("A break crossing a try/finally must first enter that finally.",
        hasEdgeTo(cfg, breakNode, innerFinally));
    assertTrue("A break crossing nested try/finally blocks must execute the outer finally.",
        isReachable(cfg, breakNode, outerFinally));
  }

  @Test
  public void testBreakThroughThreeNestedFinallyBlocksReachesOuterFinally() {
    Node breakNode = new Node(Token.BREAK);
    Node firstFinally = blockWithExpression("firstCleanup");
    Node secondFinally = blockWithExpression("secondCleanup");
    Node thirdFinally = blockWithExpression("thirdCleanup");

    Node firstTry = tryFinally(blockWith(breakNode), firstFinally);
    Node secondTry = tryFinally(blockWith(firstTry), secondFinally);
    Node thirdTry = tryFinally(blockWith(secondTry), thirdFinally);
    Node whileNode = new Node(Token.WHILE, new Node(Token.TRUE), blockWith(thirdTry));
    Node script = scriptWith(function("f", blockWith(whileNode)));

    ControlFlowGraph<Node> cfg = analyze(script);

    assertTrue(hasEdgeTo(cfg, breakNode, firstFinally));
    assertTrue(isReachable(cfg, breakNode, secondFinally));
    assertTrue("Deep break completion must retain cross edges to the outer finally.",
        isReachable(cfg, breakNode, thirdFinally));
  }

  @Test
  public void testMayThrowExceptionRecognizesThrowingExpressionsButNotFunctionBodies() {
    Node call = new Node(Token.CALL, Node.newString(Token.NAME, "mayThrow"));
    Node function = function("f", blockWith(new Node(Token.THROW, Node.newString(Token.NAME, "x"))));
    Node block = blockWith(new Node(Token.EXPR_RESULT, call));

    assertTrue(ControlFlowAnalysis.mayThrowException(call));
    assertTrue(ControlFlowAnalysis.mayThrowException(block));
    assertFalse("A nested function body belongs to a separate CFG.",
        ControlFlowAnalysis.mayThrowException(function));
  }

  @Test
  public void testIsBreakTargetHonorsLabels() {
    Node loop = new Node(Token.WHILE, new Node(Token.TRUE), blockWith(new Node(Token.BREAK)));
    Node label = new Node(Token.LABEL, Node.newString(Token.NAME, "done"), loop);

    assertNotNull(label);
    assertTrue(ControlFlowAnalysis.isBreakTarget(loop, "done"));
    assertFalse(ControlFlowAnalysis.isBreakTarget(loop, "other"));
    assertFalse(ControlFlowAnalysis.isBreakTarget(loop, null));
  }

  private static ControlFlowGraph<Node> analyze(Node script) {
    Compiler compiler = new Compiler();
    ControlFlowAnalysis analysis = new ControlFlowAnalysis(compiler, true, true);
    analysis.process(null, script);
    ControlFlowGraph<Node> cfg = compiler.getControlFlowGraph();
    assertNotNull(cfg);
    return cfg;
  }

  private static boolean hasEdgeTo(
      ControlFlowGraph<Node> cfg, Node source, Node destination) {
    for (DiGraphEdge<Node, Branch> edge : cfg.getOutEdges(source)) {
      if (edge.getDestination().getValue() == destination) {
        return true;
      }
    }
    return false;
  }

  private static boolean isReachable(
      ControlFlowGraph<Node> cfg, Node source, Node destination) {
    Set<Node> visited = new HashSet<Node>();
    ArrayDeque<Node> work = new ArrayDeque<Node>();
    work.add(source);

    while (!work.isEmpty()) {
      Node current = work.removeFirst();
      if (!visited.add(current)) {
        continue;
      }
      if (current == destination) {
        return true;
      }
      for (DiGraphEdge<Node, Branch> edge : cfg.getOutEdges(current)) {
        Node next = edge.getDestination().getValue();
        if (next != null && !visited.contains(next)) {
          work.addLast(next);
        }
      }
    }
    return false;
  }

  private static Node scriptWith(Node child) {
    Node script = new Node(Token.SCRIPT);
    script.addChildToBack(child);
    return script;
  }

  private static Node function(String name, Node body) {
    return new Node(
        Token.FUNCTION,
        Node.newString(Token.NAME, name),
        new Node(Token.PARAM_LIST),
        body);
  }

  private static Node tryFinally(Node tryBlock, Node finallyBlock) {
    return new Node(Token.TRY, tryBlock, new Node(Token.BLOCK), finallyBlock);
  }

  private static Node blockWith(Node child) {
    Node block = new Node(Token.BLOCK);
    block.addChildToBack(child);
    return block;
  }

  private static Node blockWithExpression(String name) {
    return blockWith(new Node(Token.EXPR_RESULT, Node.newString(Token.NAME, name)));
  }
}