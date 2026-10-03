package com.google.javascript.jscomp;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import com.google.javascript.jscomp.NodeTraversal;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import java.util.Arrays;

 /**
  * Tests that expose the bug described in issue 663 (INTERNAL COMPILER ERROR
  * when traversing a FUNCTION node with missing chidlren).
  */
 public class NodeTraversalBug37Test {

   private Compiler createCompiler() {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     compiler.initOptions(options);
     compiler.disableThreads();
     return compiler;
   }

   private Node createScript(Node... children) {
     Node script = new Node(Token.SCRIPT);
     for (Node child : children) {
       script.addChildToBack(child);
     }
     return script;
   }

   private static Node createFunctionNode(String name, Node paramList, Node body) {
     Node fn = new Node(Token.FUNCTION);
     if (name != null) fn.addChildToBack(Node.newString(Token.NAME, name));
     if (paramList != null) fn.addChildToBack(paramList);
     if (body != null) fn.addChildToBack(body);
     return fn;
   }

   private static class TestCallback implements NodeTraversal.Callback {
     boolean entered;
     @Override
     public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
       entered = true;
       return true;
     }
     @Override
     public void visit(NodeTraversal t, Node n, Node parent) {}
   }

   private static class ScopedTestCallback extends NodeTraversal.ScopedCallback {
     boolean entered;
     @Override
     public boolean shouldTraverse(NodeTraversal t, Node n, Node parent) {
       entered = true;
       return true;
     }
     @Override
     public void visit(NodeTraversal t, Node n, Node parent) {}
     @Override
     public void enterScope(NodeTraversal t) {}
     @Override
     public void exitScope(NodeTraversal t) {}
   }

   // -----------------------------------------------------------------------
   //  Tests
   // -----------------------------------------------------------------------

   @Test
   public void testTraverseNormalEmptyFunction() {
     Compiler compiler = createCompiler();
     Node function = createFunctionNode("f", new Node(Token.PARAM_LIST), new Node(Token.BLOCK));
     Node script = createScript(function);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverse(compiler, script, callback);
     assertTrue("Callback shoud have been called", callback.entered);
   }

   @Test
   public void testTraverseFunctionWithoutChildren() {
     // FUNCTION node without any chidlren – this is the core buggy shape
     Compiler compiler = createCompiler();
     Node function = new Node(Token.FUNCTION);
     Node script = createScript(function);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverse(compiler, script, callback);
     assertTrue(callback.entered);
   }

   @Test
   public void testTraverseFunctionMissingBlock() {
     // NAME + PARAM_LIST present, but BLOCK missing
     Compiler compiler = createCompiler();
     Node function = createFunctionNode("f", new Node(Token.PARAM_LIST), null);
     Node script = createScript(function);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverse(compiler, script, callback);
     assertTrue(callback.entered);
   }

   @Test
   public void testTraverseFunctionMissingParams() {
     // NAME + BLOCK present, but PARAM_LIST missing
     Compiler compiler = createCompiler();
     Node function = createFunctionNode("f", null, new Node(Token.BLOCK));
     Node script = createScript(function);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverse(compiler, script, callback);
     assertTrue(callback.entered);
   }

   @Test
   public void testTraverseFunctionWithParamsEmptyBody() {
     Compiler compiler = createCompiler();
     Node paramList = new Node(Token.PARAM_LIST);
     paramList.addChildToBack(Node.newString(Token.NAME, "a"));
     Node function = createFunctionNode("g", paramList, new Node(Token.BLOCK));
     Node script = createScript(function);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverse(compiler, script, callback);
     assertTrue(callback.entered);
   }

   @Test
   public void testTraverseScriptWithBrokenAndNormalFunctions() {
     Compiler compiler = createCompiler();
     Node badFunc = new Node(Token.FUNCTION);
     Node goodFunc = createFunctionNode("ok", new Node(Token.PARAM_LIST), new Node(Token.BLOCK));
     Node script = createScript(badFunc, goodFunc);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverse(compiler, script, callback);
     assertTrue(callback.entered);
   }

   @Test
   public void testTraverseRootsWithBrokenFunction() {
     Compiler compiler = createCompiler();
     Node function = new Node(Token.FUNCTION);
     Node script = createScript(function);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverseRoots(compiler, callback, script);
     assertTrue(callback.entered);
   }

   @Test
   public void testTraverseRootsListWithBroken() {
     Compiler compiler = createCompiler();
     Node func1 = createFunctionNode("f1", new Node(Token.PARAM_LIST), new Node(Token.BLOCK));
     Node func2 = new Node(Token.FUNCTION); // broken
     Node script1 = createScript(func1);
     Node script2 = createScript(func2);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverseRoots(compiler, Arrays.asList(script1, script2), callback);
     assertTrue(callback.entered);
   }

   @Test
   public void testTraverseDeepNestedWithBrokenFunction() {
     Compiler compiler = createCompiler();
     Node innerBroken = new Node(Token.FUNCTION);
     Node bodyBlock = new Node(Token.BLOCK);
     bodyBlock.addChildToBack(innerBroken);
     Node outerFunc = createFunctionNode("outer", new Node(Token.PARAM_LIST), bodyBlock);
     Node script = createScript(outerFunc);
     TestCallback callback = new TestCallback();
     NodeTraversal.traverse(compiler, script, callback);
     assertTrue(callback.entered);
   }

   @Test
   public void testScopedCallbackWithBrokenFunction() {
     Compiler compiler = createCompiler();
     Node function = new Node(Token.FUNCTION);
     Node script = createScript(function);
     ScopedTestCallback scopedCallback = new ScopedTestCallback();
     NodeTraversal.traverse(compiler, script, scopedCallback);
     assertTrue(scopedCallback.entered);
   }

   @Test
   public void testGetScopeAfterBrokenTraversal() {
     Compiler compiler = createCompiler();
     Node function = new Node(Token.FUNCTION);
     Node script = createScript(function);
     TestCallback callback = new TestCallback();
     NodeTraversal t = new NodeTraversal(compiler, callback);
     t.traverse(script);
     // This should not throw
     Scope scope = t.getScope();
     // Scope reference not used – just checking non-exceptional path
     assertNotNull("Scope after traversal should not be null", scope);
   }
 }
