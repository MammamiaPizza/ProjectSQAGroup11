package com.google.javascript.jscomp;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.collect.Lists;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**

 - Tests for {@link ReferenceCollectingCallback} that target fault-related behavior,
 - especially the block-boundary handling bug (issue 1053).
  */
 public class ReferenceCollectingCallbackTest extends TestCase {
  private Compiler compiler;
  private Node externs;
  @Override
  protected void setUp() throws Exception {
  super.setUp();
  compiler = new Compiler();
  compiler.initOptions(new CompilerOptions());
  // minimal externs
  externs = new Node(Token.SCRIPT);
  externs.setIsSyntheticBlock(true);
  }
  /**
  - Helper: parses JS, runs the default-passive callback, returns it.
    */
   private ReferenceCollectingCallback processJS(String js) {
   Node root = compiler.parseSyntheticCode("test.js", js);
   ReferenceCollectingCallback callback = new ReferenceCollectingCallback(
       compiler, ReferenceCollectingCallback.DO_NOTHING_BEHAVIOR);
   callback.process(externs, root);
   return callback;
   }
  /**
  - Helper to find a Var by name among all symbols.
    */
   private static Var findVar(ReferenceCollectingCallback callback, String name) {
   for (Var v : callback.getAllSymbols()) {
   if (v.name.equals(name)) {
       return v;
   }
   }
   return null;
   }
  /**
  - Counts the references in a collection.
    */
   private static int count(ReferenceCollectingCallback.Referenceollection rc) {
   int n = 0;
   for (@SuppressWarnings("unused") ReferenceCollectingCallback.Reference r : rc) {
   n++;
   }
   return n;
   }
  // ---------- Tests ----------
  /** A simple declaration with initialiser and a use: should be well-defined.
  */
  public void testSimpleDeclarationAndUse() {
      ReferenceCollectingCallback callback = processJS("var a = 1; var b = a;");
      Var v = findVar(callback, "a");
      assertNotNull(v);
      ReferenceCollectingCallback.Referenceollection refs = callback.getReferences(v);
      assertNotNull(refs);
      assertEquals("declaration + use", 2, count(refs));
      assertTrue("initialising declaration makes it well-defined", refs.isWellDefined());
  }
  /** Re-assigning after declaration kills well-definedness.
  */
  public void testReassignmentNotWellDefined() {
      ReferenceCollectingCallback callback = processJS("var a = 1; a = 2; a;");
      Var v = findVar(callback, "a");
      ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
      assertFalse("reassigned variable must not be well-defined", refs.isWellDefined());
  }
  /** An assignment inside a loop prevents "assigned once in lifetime".
  */
  public void testLoopAssignmentNotOnceInLifetime() {
      ReferenceCollectingCallback callback = processJS("var a = 1; while (true) { a = 2; } a;");
      Var v = findVar(callback, "a");
      ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
      assertFalse("assignment inside loop", refs.isAssignedOnceInLifetime());
  }
  /**
  - KEY REGRESSION TEST for issue-1053 / Closure-120b.
  -
  - The bug in <code>visit()</code> pops the block stack <em>before</em>
  - children are traversed.  Therefore the loop body's block stays on the
  - stack for subsequent nodes, causing <tt>a = 2</tt> to be (incorrectly)
  - considered inside the loop.  After the fix,
  - <tt>isAssignedOnceInLifetime()</tt> must return <tt>true</tt>.
    */
   public void testAssignmentAfterLoopShouldBeOnceInLifetime() {
   // The empty for-loop creates a block that should be popped after its body.
   String js = "var a = 1; for (var i = 0; i < 10; i++) {} a = 2;";
   ReferenceCollectingCallback callback = processJS(js);
   Var v = findVar(callback, "a");
   ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
   assertTrue("assignment outside loop must be single assigning occurrence",
       refs.isAssignedOnceInLifetime());
  }
  /** <code>for-in</code> LHS is treated as an initialising declaration.
  */
  public void testForInDeclarationIsWellDefined() {
      ReferenceCollectingCallback callback = processJS("for (var a in obj) { var b = a; }");
      Var v = findVar(callback, "a");
      ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
      assertTrue("for-in var is initialising", refs.isWellDefined());
      assertNotNull("must have an initializing reference",
              refs.getInitializingReference());
  }
  /** Shadowed variables are independent.
  */
  public void testShadowedVar() {
      ReferenceCollectingCallback callback = processJS(
              "var a = 1; function f() { var a = 2; a; }");
      Var outer = findVar(callback, "a");
      assertNotNull(outer);
      Var inner = null;
      for (Var v : callback.getAllSymbols()) {
          if (v.name.equals("a") && v.scope != outer.scope) {
              inner = v;
              break;
          }
      }
      assertNotNull("inner shadowed var must exist", inner);
      ReferenceCollectingCallback.ReferenceCollection innerRefs = callback.getReferences(inner);
      assertEquals(2, count(innerRefs)); // declaration + use
      assertTrue(innerRefs.isWellDefined());
  }
  /** Assignments in both branches of if/else break well-definedness.
  */
  public void testConditionalBranchAssignments() {
      ReferenceCollectingCallback callback = processJS(
              "var a = 1; if (x) { a = 2; } else { a = 3; } a;");
      Var v = findVar(callback, "a");
      ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
      assertFalse("multiple assignments across branches", refs.isWellDefined());
      assertFalse("multiple assigning references", refs.isAssignedOnceInLifetime());
  }
  /** Try / catch blocks should be separate boundaries.
  */
  public void testTryCatchBlock() {
      ReferenceCollectingCallback callback = processJS(
              "var a = 1; try { a = 2; } catch(e) { a = 3; } a;");
      Var v = findVar(callback, "a");
      ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
      assertFalse("multiple assignments in try/catch", refs.isWellDefined());
  }
  /** Global variable (declared but not initialised) is not well-defined.
  */
  public void testGlobalVarWithoutInitialiser() {
      ReferenceCollectingCallback callback = processJS("var a; a = 1;");
      Var v = findVar(callback, "a");
      ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
      assertFalse("var a has no initialiser", refs.isWellDefined());
  }
  /** Each reference carries an InputId.
  */
  public void testReferenceInputId() {
      ReferenceCollectingCallback callback = processJS("var a = 1;");
      Var v = findVar(callback, "a");
      ReferenceCollectingCallback.ReferenceCollection refs = callback.getReferences(v);
      for (ReferenceCollectingCallback.Reference r : refs) {
          assertNotNull("every reference needs an input id", r.getInputId());
      }
  }
  /** Verify that the behavior callback is invoked for each scope exit.
  */
  public void testBehaviorAfterExitScope() {
      String js = "var a = 1; function f() { var b = 2; }";
      Node root = compiler.parseSyntheticCode("test.js", js);
      TestBehavior behavior = new TestBehavior();
      ReferenceCollectingCallback callback = new ReferenceCollectingCallback(
              compiler, behavior);
      callback.process(externs, root);
      // at least the global scope and the function scope are processed
      assertTrue("behavior.afterExitScope called at least twice",
              behavior.maps.size() >= 2);
      // the last one should be the global scope
      NodeTraversal lastTraversal = behavior.traversals.get(behavior.traversals.size() - 1);
      assertNotNull(lastTraversal);
      assertNotNull("global scope exit reference map is delivered",
              behavior.maps.get(behavior.maps.size() - 1));
  }
  // ------ helper for behavior test ------
  private static class TestBehavior implements ReferenceCollectingCallback.Behavior {
      final List<ReferenceCollectingCallback.ReferenceMap> maps =
              new ArrayList<ReferenceCollectingCallback.ReferenceMap>();
      final List<NodeTraversal> traversals =
              new ArrayList<NodeTraversal>();
   @Override
   public void afterExitScope(NodeTraversal t,
                              ReferenceCollectingCallback.ReferenceMap referenceMap) {
       traversals.add(t);
       maps.add(referenceMap);
   }
  }

}