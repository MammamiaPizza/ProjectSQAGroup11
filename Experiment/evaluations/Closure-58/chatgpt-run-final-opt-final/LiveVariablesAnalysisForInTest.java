package com.google.javascript.jscomp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import org.junit.Test;

public class LiveVariablesAnalysisForInTest {

  @Test
  public void testElementExpressionInForInMarksLocalOperandsLive() {
    AnalysisContext context =
        createAnalysis("function f(a, b, c) { for (a[b] in c) {} }");

    LiveVariablesAnalysis.LiveVariableLattice result =
        context.analysis.flowThrough(
            context.forNode, context.analysis.createEntryLattice());

    assertTrue(result.isLive(context.scope.getVar("a")));
    assertTrue(result.isLive(context.scope.getVar("b")));
    assertTrue(result.isLive(context.scope.getVar("c")));
  }

  @Test
  public void testPropertyExpressionInForInHandlesNonLocalName() {
    AnalysisContext context =
        createAnalysis("function f(local) { for (external.property in local) {} }");

    LiveVariablesAnalysis.LiveVariableLattice result =
        context.analysis.flowThrough(
            context.forNode, context.analysis.createEntryLattice());

    assertTrue(result.isLive(context.scope.getVar("local")));
  }

  @Test
  public void testNamedForInVariableAndIterableAreLive() {
    AnalysisContext context =
        createAnalysis("function f(key, object) { for (key in object) {} }");

    LiveVariablesAnalysis.LiveVariableLattice result =
        context.analysis.flowThrough(
            context.forNode, context.analysis.createEntryLattice());

    assertTrue(result.isLive(context.scope.getVar("key")));
    assertTrue(result.isLive(context.scope.getVar("object")));
  }

  private AnalysisContext createAnalysis(String source) {
    Compiler compiler = new Compiler();
    compiler.initOptions(new CompilerOptions());

    Node script = compiler.parseTestCode(source);
    Node function = findFirstNodeOfType(script, Token.FUNCTION);
    Node forNode = findFirstNodeOfType(function, Token.FOR);

    assertNotNull(function);
    assertNotNull(forNode);

    Scope scope = new SyntacticScopeCreator(compiler).createScope(function, null);
    ControlFlowAnalysis controlFlowAnalysis =
        new ControlFlowAnalysis(compiler, false, false);
    controlFlowAnalysis.process(null, function.getLastChild());

    LiveVariablesAnalysis analysis =
        new LiveVariablesAnalysis(controlFlowAnalysis.getCfg(), scope, compiler);
    return new AnalysisContext(analysis, scope, forNode);
  }

  private Node findFirstNodeOfType(Node node, int type) {
    if (node == null) {
      return null;
    }
    if (node.getType() == type) {
      return node;
    }
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      Node found = findFirstNodeOfType(child, type);
      if (found != null) {
        return found;
      }
    }
    return null;
  }

  private static final class AnalysisContext {
    final LiveVariablesAnalysis analysis;
    final Scope scope;
    final Node forNode;

    AnalysisContext(LiveVariablesAnalysis analysis, Scope scope, Node forNode) {
      this.analysis = analysis;
      this.scope = scope;
      this.forNode = forNode;
    }
  }
}
