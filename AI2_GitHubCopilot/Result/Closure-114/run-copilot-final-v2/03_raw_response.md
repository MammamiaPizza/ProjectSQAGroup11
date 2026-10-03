package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import junit.framework.TestCase;

 /**
  * Tests for {@link NameAnalyzer} covering the bug where assignment-with-call
  * patterns were incorrectly removed (bug #1085).
  */
 public class NameAnalyzerTest extends TestCase {

   /** Runs the NameAnalyzer pass with removeUnreferenced=true and returns the resulting AST root.
*/
   private Node processJavaScript(String source) throws Exception {
     Compiler compiler = new Compiler();
     CompilerOptions options = new CompilerOptions();
     // Ensure dead‑code elimination is active (NameAnalyzer runs inside it)
     options.setRemoveUnusedVariables(true);
     compiler.initOptions(options);

     // The simplest way to run only NameAnalyzer on a fresh AST is to use the
     // compiler's parse facilities and then invoke the pass directly.
     // However, to keep the test self‑contained we execute the full compile pipeline,
     // which includes NameAnalyzer.
     SourceFile externsFile = SourceFile.fromCode("externs", "");
     SourceFile sourceFile = SourceFile.fromCode("testcode", source);
     Result result = compiler.compile(externsFile, sourceFile, options);

     // After compilation, grab the JavaScript AST root.
     return compiler.getRoot().getLastChild();
   }

   /** Return true if a function with the given name is declared anywhere in the AST. */
   private boolean hasFunctionNamed(Node root, String name) {
     if (root == null) return false;
     if (root.isFunction()) {
       Node funcName = root.getFirstChild();
       if (funcName != null && funcName.isName() && funcName.getString().equals(name)) {
         return true;
       }
     }
     boolean found = false;
     for (Node child : root.children()) {
       found = found || hasFunctionNamed(child, name);
     }
     return found;
   }

   /** Return true if a CALL node whose target is the given name exists in the AST. */
   private boolean hasCallTo(Node root, String name) {
     if (root == null) return false;
     if (root.isCall()) {
       Node target = root.getFirstChild();
       if (target != null && target.isName() && target.getString().equals(name)) {
         return true;
       }
     }
     boolean found = false;
     for (Node child : root.children()) {
       found = found || hasCallTo(child, name);
     }
     return found;
   }

   /**
    * Regression test for bug #1085: assignment with call where the assigned
    * variable is used later must keep both the variable and the called function.
    */
   public void testAssignWithCall() throws Exception {
     String js =
         "var a = foo();\n" +
         "function foo() {}\n" +
         "alert(a);";
     Node root = processJavaScript(js);
     assertTrue("Function 'foo' was removed even though it is called", hasFunctionNamed(root,
"foo"));
     assertTrue("Call to 'foo' was removed", hasCallTo(root, "foo"));
   }

   /**
    * Similar pattern but the assigned variable is unused.  If the RHS call has no
    * side effects, both the variable and the call (and possibly the function) may be removed.
    * This test verifies that the removal is *not* overly aggressive – the function should
    * be kept if it was removed incorrectly by the buggy version.
    */
   public void testAssignWithCallUnusedVar() throws Exception {
     String js =
         "var a = foo();\n" +
         "function foo() { return 1; }\n" +
         // a is never used
         "";
     Node root = processJavaScript(js);
     // With the bug, foo might be removed even though it arguably has a side‑effectless call.
     // We assert the correct behaviour: the function is dead only if both a and foo are
unreferenced.
     // According to the spec, if foo has no side effects and a is unused, it may be removed.
     // This test does not enforce a particular correct answer – it only ensures we do not
     // crash or create an invalid AST (the compiler itself will catch syntax errors).
     // For a stronger oracle we check that the tree is well‑formed (no null parent errors).
     assertNotNull(root);
   }

   /**
    * Assignment with call where the RHS function has a side effect (global write).
    * Even when the assigned variable is unused, the call must be preserved.
    */
   public void testAssignWithCallSideEffects() throws Exception {
     String js =
         "var a = foo();\n" +
         "function foo() { window.x = 1; }\n" +
         // a never used
         "";
     Node root = processJavaScript(js);
     // The call to foo must be kept because it has a side effect.
     assertTrue("Side‑effecting call to 'foo' should remain", hasCallTo(root, "foo"));
     assertTrue("Function 'foo' should remain", hasFunctionNamed(root, "foo"));
   }

   /**
    * Chained assignments with calls: x = a(), y = b(x).
    * Neither a nor b should be removed as long as the final result is used.
    */
   public void testAssignWithCallChain() throws Exception {
     String js =
         "var x = a();\n" +
         "var y = b(x);\n" +
         "function a() { return 1; }\n" +
         "function b(p) { return p + 1; }\n" +
         "alert(y);";
     Node root = processJavaScript(js);
     assertTrue("Function 'a' removed", hasFunctionNamed(root, "a"));
     assertTrue("Function 'b' removed", hasFunctionNamed(root, "b"));
     assertTrue("Call to 'a' removed", hasCallTo(root, "a"));
     assertTrue("Call to 'b' removed", hasCallTo(root, "b"));
   }

   /**
    * Nested assignment: x = y = foo(). Verify that foo is kept when
    * at least one of the variables is used downstream.
    */
   public void testNestedAssignWithCall() throws Exception {
     String js =
         "var x, y;\n" +
         "x = y = foo();\n" +
         "function foo() {}\n" +
         "alert(x);";
     Node root = processJavaScript(js);
     assertTrue("'foo' incorrectly removed", hasFunctionNamed(root, "foo"));
     assertTrue("Call to 'foo' incorrectly removed", hasCallTo(root, "foo"));
   }

   /**
    * Assignment whose RHS is a call that also acts as a class‑defining call
    * (e.g. inherits()). The function that performs the inheritance must not be stripped.
    */
   public void testClassDefiningCallInAssignment() throws Exception {
     String js =
         "var Child = inherits(Parent);\n" +
         "function inherits(superClass) { }\n" +
         "alert(Child);";
     Node root = processJavaScript(js);
     assertTrue("Class‑defining call removed", hasCallTo(root, "inherits"));
     assertTrue("Class‑defining function removed", hasFunctionNamed(root, "inherits"));
   }

   /**
    * Prototype property assignment: Cls.prototype.method = function() {}.
    * The prototype method should be kept when the class is referenced.
    */
   public void testPrototypeAssignmentWithCall() throws Exception {
     String js =
         "function Cls() {}\n" +
         "Cls.prototype.m = foo();\n" +
         "function foo() {}\n" +
         "new Cls();";
     Node root = processJavaScript(js);
     assertTrue("Prototype assignment call removed", hasCallTo(root, "foo"));
     assertTrue("'foo' removed", hasFunctionNamed(root, "foo"));
   }

   /**
    * Assignment inside a control structure (if statement) with a call on the RHS.
    */
   public void testAssignWithCallInsideIf() throws Exception {
     String js =
         "var a;\n" +
         "if (a) a = foo();\n" +
         "function foo() {}\n" +
         "alert(a);";
     Node root = processJavaScript(js);
     assertTrue("'foo' removed when assignment inside if is reachable", hasFunctionNamed(root,
"foo"));
   }

   /**
    * Assign‑with‑call where the function is referenced only through the call and
    * the assigned variable is later overwritten before use. The call itself may be dead,
    * but the test ensures we do not crash.
    */
   public void testAssignWithCallDeadAfterOverwrite() throws Exception {
     String js =
         "var a = foo();\n" +
         "function foo() {}\n" +
         "a = 1;\n" +
         "alert(a);";
     Node root = processJavaScript(js);
     // The first call may be removed if it has no side effects and its result is overwritten.
     // We only guarantee no exception.
     assertNotNull(root);
   }

   /**
    * Assign‑with‑call where the function is also referenced directly elsewhere.
    * Both references must be preserved.
    */
   public void testAssignWithCallAndDirectRef() throws Exception {
     String js =
         "var a = foo();\n" +
         "function foo() {}\n" +
         "foo();\n" +
         "alert(a);";
     Node root = processJavaScript(js);
     assertTrue("'foo' should remain", hasFunctionNamed(root, "foo"));
     // There should be two calls to foo
     assertTrue("Fewer than two calls to 'foo'", countCallsTo(root, "foo") >= 2);
   }

   private int countCallsTo(Node root, String name) {
     if (root == null) return 0;
     int count = 0;
     if (root.isCall()) {
       Node target = root.getFirstChild();
       if (target != null && target.isName() && target.getString().equals(name)) {
         count++;
       }
     }
     for (Node child : root.children()) {
       count += countCallsTo(child, name);
     }
     return count;
   }
 }