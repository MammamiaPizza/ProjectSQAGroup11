package com.google.javascript.jscomp;

 import com.google.common.collect.Sets;
 import java.util.Set;
 import junit.framework.TestCase;

 /**
  * Tests for {@link FunctionInjector} focusing on inlining decisions.
  */
 public class FunctionInjectorTest extends TestCase {
     private Compiler compiler;
     private FunctionInjector injector;

     @Override
     protected void setUp() throws Exception {
         super.setUp();
         compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();
         options.setCheckSymbols(true);
         compiler.init(options);
         injector = new FunctionInjector(
             compiler,
             compiler.getUniqueNameIdSupplier(),
             false, // allowDecomposition
             false, // assumeStrictThis
             false); // assumeMinimumCapture
 }

     private static class NoOpCallback implements NodeTraversal.Callback {
         @Override
         public boolean shouldTraverse(NodeTraversal nodeTraversal, Node n, Node parent) {
             return true;
         }
         @Override
         public void visit(NodeTraversal nodeTraversal, Node n, Node parent) {
         }
     }

     private NodeTraversal createAndTraverse(Node scriptRoot) {
         NodeTraversal t = new NodeTraversal(compiler, new NoOpCallback());
         t.traverse(scriptRoot);
         return t;
     }

     private Node parseSyntheticCode(String js) {
         return compiler.parseSyntheticCode("testcode", js);
     }

     private Node findNode(Node root, int tokenType) {
         if (root.getType() == tokenType) {
             return root;
         }
         for (Node child = root.getFirstChild(); child != null; child = child.getNext()) {
             Node result = findNode(child, tokenType);
             if (result != null) {
                 return result;
             }
         }
         return null;
     }

     /**
      * Helper that finds the first CALL node and the function node under the script root,
      * then checks canInlineReferenceToFunction.
      */
     private CanInlineResult checkCanInline(String script, boolean referencesThis, boolean
containsFunctions) {
         Node root = parseSyntheticCode(script);
         Node fnNode = root.getFirstChild(); // function declaration
         Node callNode = findNode(root, Token.CALL);
         assertNotNull("CALL node not found", callNode);
         NodeTraversal t = createAndTraverse(root);
         Set<String> needAliases = Sets.newHashSet();
         return injector.canInlineReferenceToFunction(
             t, callNode, fnNode, needAliases, InliningMode.DIRECT, referencesThis,
containsFunctions);
     }

     /** A function call where the argument has a side effect (increment) should not be inlined. */
     public void testIssue1101a_sideEffectIncrement() {
         CanInlineResult result = checkCanInline("function f(x){return x;} f(a++);", false, false);
         assertEqual(CanInlineResult.NO, result);
     }

     /** Argument with a call expression that could have side effects should be rejected. */
     public void testIssue1101b_sideEffectCall() {
         CanInlineResult result = checkCanInline("function f(x){return x;} f(g());", false, false);
         assertEqual(CanInlineResult.NO, result);
     }

     /** Simple constant argument should be inlinable. */
     public void testSimpleInlining() {
         CanInlineResult result = checkCanInline("function f(x){return x+1;} f(1);", false, false);
         assertEqual(CanInlineResult.YES, result);
     }

     /** Recursive function that calls itself must not be inlined. */
     public void testRecursiveFunction() {
         CanInlineResult result = checkCanInline("function f(x){return f(x-1);} f(5);", false,
false);
         assertEqual(CanInlineResult.NO, result);
     }

     /** Function that references "arguments" must not be inlined. */
     public void testArgumentsReference() {
         CanInlineResult result = checkCanInline("function f(){return arguments[0];} f(1);", false,
false);
         assertEqual(CanInlineResult.NO, result);
     }

     /** Function that calls eval must not be inlined. */
     public void testEvalReference() {
         CanInlineResult result = checkCanInline("function f(){return eval('1');} f();", false,
false);
         assertEqual(CanInlineResult.NO, result);
     }

     /** The parameter is modified inside an if condition: inlining with a side-effect arg is
unsafe. */
     public void testInlineIfParametersModified8() {
         CanInlineResult result = checkCanInline("function f(x){if(x++){return x;} return x;}
f(a++);", false, false);
         assertEqual(CanInlineResult.NO, result);
     }

     /** A variant where the parameter is conditionally mutated – also should be rejected. */
     public void testInlineIfParametersModified9() {
         CanInlineResult result = checkCanInline("function f(x){while(x--){} return x;} f(a);",
false, false);
         assertEqual(CanInlineResult.NO, result);
     }

     /** Function containing an inner function in global scope may be inlinable. */
     public void testContainsFunctionsGlobalScope() {
         CanInlineResult result = checkCanInline("function f(){return function g(){};} f();", false,
true);
         assertEqual(CanInlineResult.YES, result);
     }

     /** Double inlining of the same function: the second call may be unsafe after the first inline.
*/
     public void testDoubleInlining2() {
         // The second call to f(2) should be rejected (or at least the pair must be handled
carefully).
         // We verify that the second CALL node is not inlinable when the function identity is
altered.
         String script = "function f(x){return x;} f(1)+f(2);";
         Node root = parseSyntheticCode(script);
         Node fnNode = root.getFirstChild();
         NodeTraversal t = createAndTraverse(root);
         // Find the second CALL node.
         Node child = root.getFirstChild(); // function
         Node exprStmt = child.getNext();   // EXPR_RESULT for plus
         Node plusNode = exprStmt.getFirstChild(); // ADD node
         Node call1 = plusNode.getFirstChild(); // first f(1)
         Node call2 = call1.getNext();           // second f(2)
         assertNotNull("second call node missing", call2);
         assertTrue(call2.isCall());
         Set<String> needAliases = Sets.newHashSet();
         CanInlineResult result = injector.canInlineReferenceToFunction(t, call2, fnNode,
needAliases, InliningMode.DIRECT, false, false);
         // The bug may return YES incorrectly; we expect NO because of the already-inlined context.
         assertEqual(CanInlineResult.NO, result);
     }

     /** Function using fn.call should be treated as unsupported call type (rejected). */
     public void testUnsupportedCallType() {
         CanInlineResult result = checkCanInline("function f(){} f.call(null);", false, false);
         // fn.call is actually supported by isSupportedCallType, but if side effects exist, it may
still be NO.
         // We adjust to test a truly unsupported form: the callee is not a name.
         script = "function f(){} (f)();";
         result = checkCanInline(script, false, false);
         assertEqual(CanInlineResult.NO, result);
     }
 }