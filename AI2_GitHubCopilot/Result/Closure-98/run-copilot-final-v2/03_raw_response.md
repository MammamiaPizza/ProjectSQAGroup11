package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;
 import com.google.javascript.jscomp.Scope.Var;
 import com.google.javascript.jscomp.ReferenceCollectingCallback.ReferenceCollection;
 import com.google.javascript.jscomp.ReferenceCollectingCallback.Behavior;
 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.CompilerOptions;
 import junit.framework.TestCase;

 /**
  * Tests for ReferenceCollectingCallback focusing on loop-related assignment detection.
  * The bug (issue 174) causes incorrect block-boundary tracking inside for-loops,
  * leading to isAssignedOnceInLifetime returning true when assignments are inside loops.
  */
 public class ReferenceCollectingCallbackTest extends TestCase {

     // ----------- Tests for isAssignedOnceInLifetime -----------

     /**
      * A variable assigned once inside a for-loop body must NOT be considered
      * assigned-once-in-lifetime. The buggy version returns true here because
      * it does not check whether the assignment is inside a loop.
      */
     public void testIsAssignedOnceInLifetime_ForLoop() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var x; for (var i=0; i<5; i++) { x = i; } }", "x");
         assertFalse("Assignment inside a loop should not be considered assigned-once-in-lifetime",
                 refs.isAssignedOnceInLifetime());
     }

     /**
      * A variable assigned exactly once, outside any loop, should be considered
      * assigned-once-in-lifetime.
      */
     public void testIsAssignedOnceInLifetime_NoLoop() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var x = 1; }", "x");
         assertTrue("A single assignment outside a loop should be assigned-once-in-lifetime",
                 refs.isAssignedOnceInLifetime());
     }

     /**
      * Multiple assignments to the same variable mean it is not assigned-once-in-lifetime.
      */
     public void testIsAssignedOnceInLifetime_MultipleAssignments() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var x; x=1; x=2; }", "x");
         assertFalse("Multiple assignments disqualify assigned-once-in-lifetime",
                 refs.isAssignedOnceInLifetime());
     }

     /**
      * A variable assigned inside a for-in loop (aliased) must not be
      * considered assigned-once-in-lifetime.
      */
     public void testIsAssignedOnceInLifetime_ForInLoop() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var obj={}; var x = obj; for (var y in x) { x = y; } }", "x");
         assertFalse("Assignment inside a for-in loop blocks single-assignment status",
                 refs.isAssignedOnceInLifetime());
     }

     /**
      * Null check: a variable that is never assigned has no assignments,
      * so isAssignedOnceInLifetime should be false.
      */
     public void testIsAssignedOnceInLifetime_NeverAssigned() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var x; }", "x");
         assertFalse("Never-assigned variable cannot be assigned-once-in-lifetime",
                 refs.isAssignedOnceInLifetime());
     }

     // ----------- Tests for isWellDefined -----------

     /**
      * A variable that is assigned before any use is well-defined.
      */
     public void testIsWellDefined_AssignedBeforeUse() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var x = 1; x; }", "x");
         assertTrue("Variable assigned before use should be well-defined",
                 refs.isWellDefined());
     }

     /**
      * A variable that is used before being assigned is not well-defined.
      */
     public void testIsWellDefined_UseBeforeAssign() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var x; x; x=1; }", "x");
         assertFalse("Variable used before assignment should not be well-defined",
                 refs.isWellDefined());
     }

     /**
      * A variable only declared (no assignment, no use) is arguably well-defined.
      */
     public void testIsWellDefined_OnlyDeclared() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { var x; }", "x");
         assertTrue("Declared-only variable is vacuously well-defined",
                 refs.isWellDefined());
     }

     // ----------- Boundary / basic-block consistency -----------

     /**
      * Variables declared inside a for-loop initializer and used in the loop body
      * should have a well-defined reference collection.  The block-boundary bug
      * would cause mis-placed basic blocks, potentially making isWellDefined
      * return false when it should return true.
      */
     public void testForLoopVarInitIsWellDefined() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { for (var i=0; i<5; i++) { i; } }", "i");
         assertTrue("Loop-initializer variable should be well-defined inside the loop",
                 refs.isWellDefined());
     }

     /**
      * A variable declared in the entire for-loop header (init/condition/increment)
      * and only used there should be considered well-defined.
      */
     public void testForLoopHeaderVarIsWellDefined() throws Exception {
         ReferenceCollection refs = collectReferences(
                 "function f() { for (var j=0; j<10; j++) {} }", "j");
         assertTrue("Variable used only in loop header should be well-defined",
                 refs.isWellDefined());
     }

     // ----------- Helper methods -----------

     /**
      * Compiles the given JavaScript, runs ReferenceCollectingCallback, and returns
      * the ReferenceCollection for the first variable found with the given name
      * in the innermost scope.
      */
     private ReferenceCollection collectReferences(String js, String varName) throws Exception {
         Compiler compiler = new Compiler();
         compiler.initOptions(new CompilerOptions());
         Node script = compiler.parseTestCode(js);
         Node externs = new Node(Token.SCRIPT);

         ReferenceCollectingCallback callback =
                 new ReferenceCollectingCallback(compiler, Behavior.DO_NOTHING_BEHAVIOR);
         callback.process(externs, script);

         // Find a scope that contains varName; prefer the innermost function scope.
         Var variable = findVar(compiler, script, varName);
         assertNotNull("Variable " + varName + " not found in any scope", variable);
         return callback.getReferenceCollection(variable);
     }

     /** Asks the compiler for the Var by walking the AST to find a relevant node. */
     private Var findVar(Compiler compiler, Node root, String name) {
         if (root.getType() == Token.NAME && root.getString().equals(name)) {
             Scope scope = compiler.getScope(root);
             if (scope != null) {
                 Var var = scope.getVar(name);
                 if (var != null) return var;
             }
         }
         for (Node child = root.getFirstChild(); child != null; child = child.getNext()) {
             Var result = findVar(compiler, child, name);
             if (result != null) return result;
         }
         return null;
     }
 }