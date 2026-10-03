package com.google.javascript.jscomp;

 import com.google.common.base.Preconditions;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import junit.framework.TestCase;

 import java.util.*;
 import java.util.concurrent.atomic.AtomicInteger;

 /**
  * Tests for {@link PeepholeOptimizationsPass} covering retraversal, state-stack,
  * and cascading-change scenarios exposed by bug #787.
  */
 public class PeepholeOptimizationsPassTest extends TestCase {

     // --------------- mock infrastructure ---------------

     /** Minimal concrete compiler that supports change-handler registration. */
     private static final class StubCompiler extends AbstractCompiler {
         private final List<CodeChangeHandler> handlers = new ArrayList<CodeChangeHandler>();

         @Override public void addChangeHandler(CodeChangeHandler h) { handlers.add(h); }
         @Override public void removeChangeHandler(CodeChangeHandler h) { handlers.remove(h); }
         @Override public void reportChange() {
             for (CodeChangeHandler h : handlers) h.reportChange();
         }
         // stubs for remaining abstract methods
         @Override public CompilerOptions getOptions() { throw new UnsupportedOperationException();
}
         @Override public JSError[] getWarnings() { throw new UnsupportedOperationException(); }
         @Override public JSError[] getErrors() { throw new UnsupportedOperationException(); }
         @Override public Node getRoot() { throw new UnsupportedOperationException(); }
         @Override public String toSource() { throw new UnsupportedOperationException(); }
         @Override public void compile(SourceFile externs, SourceFile input,
                                      CompilerOptions options) { throw new
UnsupportedOperationException(); }
         @Override public Result getResult() { throw new UnsupportedOperationException(); }
         @Override public CodeBuilder getCodeBuilder() { throw new UnsupportedOperationException();
}
         @Override public ErrorManager getErrorManager() { throw new
UnsupportedOperationException(); }
         @Override public void setErrorManager(ErrorManager mgr) { throw new
UnsupportedOperationException(); }
         @Override public Tracer newTracer(String n, int i) { throw new
UnsupportedOperationException(); }

         List<CodeChangeHandler> getHandlers() { return new ArrayList<CodeChangeHandler>(handlers);
}
     }

     /** Optimization that counts visits per node and optionally replaces inner functions. */
     private static final class CountingOptimization extends AbstractPeepholeOptimization {
         final Map<Node, Integer> visitCounts = new HashMap<Node, Integer>();
         private AbstractCompiler compiler;
         private final String targetName;
         private final boolean replace;

         CountingOptimization(String targetName, boolean replace) {
             this.targetName = targetName;
             this.replace = replace;
         }

         @Override public void beginTraversal(AbstractCompiler compiler) {
             this.compiler = compiler;
             visitCounts.clear();
             super.beginTraversal(compiler);
         }

         @Override public Node optimizeSubtree(Node node) {
             Integer old = visitCounts.get(node);
             visitCounts.put(node, old == null ? 1 : old + 1);
             if (replace && node.isFunction() && node.getFirstChild() != null
                 && node.getFirstChild().isName() &&
                 targetName.equals(node.getFirstChild().getString())) {
                 if (visitCounts.get(node) == 1) {
                     Node replacement = Node.newNumber(42.0);
                     node.getParent().replaceChild(node, replacement);
                     compiler.reportChange();
                     return replacement;
                 }
             }
             return node;
         }

         int count(Node n) {
             Integer c = visitCounts.get(n);
             return c == null ? 0 : c;
         }
     }

     /** Optimization that repeatedly replaces a script node to force iteration overflow. */
     private static final class OverflowOptimization extends AbstractPeepholeOptimization {
         private AbstractCompiler compiler;
         private int limit = 10001; // exceed the 10000 threshold

         OverflowOptimization() {}

         @Override public void beginTraversal(AbstractCompiler compiler) {
             this.compiler = compiler;
             super.beginTraversal(compiler);
         }

         @Override public Node optimizeSubtree(Node node) {
             if (node.isScript() && limit-- > 0) {
                 Node newScript = new Node(Token.SCRIPT);
                 node.getParent().replaceChild(node, newScript);
                 compiler.reportChange();
                 return newScript;
             }
             return node;
         }
     }

     /** Optimization that tracks begin/end calls. */
     private static final class LifecycleOptimization extends AbstractPeepholeOptimization {
         boolean started, ended;
         @Override public void beginTraversal(AbstractCompiler c) { started = true;
super.beginTraversal(c); }
         @Override public void endTraversal(AbstractCompiler c) { ended = true;
super.endTraversal(c); }
         @Override public Node optimizeSubtree(Node n) { return n; }
     }

     // --------------- node-build helpers ---------------

     private static Node name(String ident) {
         return Node.newString(Token.NAME, ident);
     }

     private static Node script() {
         return new Node(Token.SCRIPT);
     }

     private static Node function(String funcName, Node body) {
         Node func = new Node(Token.FUNCTION);
         func.addChildToBack(name(funcName));
         func.addChildToBack(new Node(Token.LP)); // parameter list
         func.addChildToBack(body);
         return func;
     }

     private static Node block(Node... stmts) {
         Node blk = new Node(Token.BLOCK);
         for (Node s : stmts) blk.addChildToBack(s);
         return blk;
     }

     private static Node returnStmt(Node expr) {
         Node ret = new Node(Token.RETURN);
         ret.addChildToBack(expr);
         return ret;
     }

     /** Builds script containing outer function with nested inner function. */
     private Node buildScriptWithNestedFunctions() {
         Node innerBody = block(returnStmt(Node.newNumber(42.0)));
         Node inner = function("inner", innerBody);
         Node outerBody = block(inner);
         Node outer = function("outer", outerBody);
         Node sc = script();
         sc.addChildToBack(outer);
         return sc;
     }

     // --------------- tests ---------------

     public void testRetraverseOnChangeInChildScope() {
         Node root = buildScriptWithNestedFunctions();
         Node outer = root.getFirstChild();
         CountingOptimization opt = new CountingOptimization("inner", true);
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(new StubCompiler(), opt);

         pass.process(null, root);

         // Bug #787: parent scope should be revisited after child modification
         assertTrue("outer function must be revisited after inner change",
                    opt.count(outer) >= 2);
     }

     public void testNoRetraverseWithoutChange() {
         Node root = buildScriptWithNestedFunctions();
         Node outer = root.getFirstChild();
         CountingOptimization opt = new CountingOptimization("inner", false); // no replace
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(new StubCompiler(), opt);

         pass.process(null, root);

         assertEquals("without change, outer visited exactly once", 1, opt.count(outer));
     }

     public void testMultipleChangesSameScope() {
         Node root = script();
         CountingOptimization opt = new CountingOptimization("none", false) {
             @Override public Node optimizeSubtree(Node node) {
                 super.optimizeSubtree(node);
                 if (node.isScript() && visitCounts.get(node) <= 3) {
                     Node replacement = Node.newNumber(0);
                     (node.getParent() != null ?
                         node.getParent().replaceChild(node, replacement) : (root = replacement));
                     compiler.reportChange();
                     return replacement;
                 }
                 return node;
             }
         };
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(new StubCompiler(), opt);

         pass.process(null, root);

         assertTrue("script visited multiple times", opt.count(root) >= 3);
     }

     public void testTraverseChildScopesFlagAfterRetraverse() {
         // When a scope is revisited, traverseChildScopes = false so nested scopes
         // are skipped. Verify inner function is NOT revisited during outer retraversal.
         // We design an opt that causes outer retraversal but in that retraversal inner is skipped.
         // This tests the state stack flag behavior indirectly.
         Node root = buildScriptWithNestedFunctions();
         CountingOptimization opt = new CountingOptimization("inner", true);
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(new StubCompiler(), opt);

         pass.process(null, root);

         Node inner = null;
         for (Node n : opt.visitCounts.keySet()) {
             if (n.isFunction() && n.getFirstChild().isName()
                 && "inner".equals(n.getFirstChild().getString())) {
                 inner = n;
                 break;
             }
         }
         // inner may have been replaced; we only care that the original function was visited once
         // (no extra visit from outer retraversal)
         assertNotNull(inner);
         assertEquals("inner visited only once despite outer retraversal", 1, opt.count(inner));
     }

     public void testTooManyIterationsThrows() {
         Node root = script();
         OverflowOptimization opt = new OverflowOptimization();
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(new StubCompiler(), opt);

         try {
             pass.process(null, root);
             fail("Expected IllegalStateException for too many iterations");
         } catch (IllegalStateException e) {
             assertTrue("message contains 'too many iterations'",
                        e.getMessage().contains("too many iterations"));
         }
     }

     public void testProcessIdempotency() {
         Node root = buildScriptWithNestedFunctions();
         StubCompiler compiler = new StubCompiler();
         CountingOptimization opt = new CountingOptimization("inner", true);
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(compiler, opt);

         pass.process(null, root);
         String first = root.toStringTree();

         opt.visitCounts.clear();
         pass.process(null, root);
         String second = root.toStringTree();

         assertEquals("subsequent process() must not alter AST", first, second);
     }

     public void testBeginEndTraversalCalled() {
         Node root = script();
         LifecycleOptimization opt = new LifecycleOptimization();
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(new StubCompiler(), opt);

         assertFalse(opt.started);
         assertFalse(opt.ended);
         pass.process(null, root);
         assertTrue("beginTraversal was called", opt.started);
         assertTrue("endTraversal was called", opt.ended);
     }

     public void testChangeHandlerRemovedAfterProcess() {
         StubCompiler compiler = new StubCompiler();
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(compiler,
             new CountingOptimization("none", false));

         assertEquals("no handlers before process", 0, compiler.getHandlers().size());
         pass.process(null, script());
         assertEquals("handler removed after process", 0, compiler.getHandlers().size());
     }

     public void testEmptyScript() {
         Node root = script();
         StubCompiler compiler = new StubCompiler();
         CountingOptimization opt = new CountingOptimization("none", false);
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(compiler, opt);

         pass.process(null, root); // must not throw
         assertEquals("script visited once", 1, opt.count(root));
     }

     public void testDeeplyNestedCascadeChange() {
         // outer -> inner1 -> inner2 ; modify inner2, expect inner1 revisited, and outer too.
         Node inner2Body = block(returnStmt(Node.newNumber(1)));
         Node inner2 = function("inner2", inner2Body);
         Node inner1Body = block(inner2);
         Node inner1 = function("inner1", inner1Body);
         Node outerBody = block(inner1);
         Node outer = function("outer", outerBody);
         Node root = script();
         root.addChildToBack(outer);

         StubCompiler compiler = new StubCompiler();
         CountingOptimization opt = new CountingOptimization("inner2", true);
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(compiler, opt);

         pass.process(null, root);

         // Bug: without proper cascade, outer may be visited only once.
         assertTrue("outer revisited after deep change", opt.count(outer) >= 2);
     }

     public void testScriptScopeRetraverseAfterChange() {
         // Change in script scope itself should trigger retraversal.
         Node root = script();
         CountingOptimization opt = new CountingOptimization("none", false) {
             @Override public Node optimizeSubtree(Node node) {
                 super.optimizeSubtree(node);
                 if (node.isScript() && visitCounts.get(node) == 1) {
                     Node replacement = script();
                     (node.getParent() != null ?
                         node.getParent().replaceChild(node, replacement) : (root = replacement));
                     compiler.reportChange();
                     return replacement;
                 }
                 return node;
             }
         };
         PeepholeOptimizationsPass pass = new PeepholeOptimizationsPass(new StubCompiler(), opt);

         pass.process(null, root);

         assertTrue("script retraversed after change", opt.count(root) >= 2);
     }
 }