package com.google.javascript.jscomp;

 /**
  * Tests for {@link FlowSensitiveInlineVariables}.
  * Covers the bug 965: variable assignment inside a hook (?:) must not block safe inlining.
  */
 public class FlowSensitiveInlineVariablesTest extends CompilerTestCase {

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     // Enable normalization so scopes / CFG are built properly.
     enableNormalize();
   }

   @Override
   protected CompilerPass getProcessor(final Compiler compiler) {
     // Invoke FlowSensitiveInlineVariables as the only optimization pass.
     return new CompilerPass() {
       @Override
       public void process(Node externs, Node root) {
         FlowSensitiveInlineVariables pass = new FlowSensitiveInlineVariables(compiler);
         pass.process(externs, root);
       }
     };
   }

   // ---------- Hook-related tests (Bug 965) ----------

   /**
    * Exact reproduction from issue 965.
    * A var is assigned inside both branches of a hook operator; the assignment
    * RHS is the same in both branches, making inlining safe.
    */
   public void testVarAssignInsideHookIssue965() {
     test(
         "function f() { var a = 1; var b = g() ? (a = 2, 3) : (a = 2, 4); alert(a); }",
         "function f() { var b = g() ? (3) : (4); alert(2); }");
   }

   /**
    * Hook assignment with different RHS in the two branches: NOT safe to inline.
    */
   public void testHookAssignDifferentRHS() {
     testSame(
         "function f() { var a = 1; var b = g() ? (a = 2, 3) : (a = 3, 4); alert(a); }");
   }

   /**
    * Hook where only one branch assigns the variable: use after hook should
    * not be blindly inlined because the other branch may leave the previous value.
    */
   public void testHookAssignOneBranchOnly() {
     testSame(
         "function f() { var a = 1; var b = g() ? (a = 2, 3) : 4; alert(a); }");
   }

   /**
    * Nested hooks with assignment in the innermost branches, same RHS.
    */
   public void testNestedHookSameRHS() {
     test(
         "function f() { var a = 1; var b = c() ? (d() ? (a = 5, 6) : (a = 5, 7)) : (a = 5, 8);
alert(a); }",
         "function f() { var b = c() ? (d() ? (6) : (7)) : (8); alert(5); }");
   }

   /**
    * Nested hooks with different RHS values – must not inline.
    */
   public void testNestedHookDifferentRHS() {
     testSame(
         "function f() { var a = 1; var b = c() ? (d() ? (a = 5, 6) : (a = 6, 7)) : (a = 7, 8);
alert(a); }");
   }

   /**
    * Hook assignment where the assigned variable is used inside the hook itself
    * (read before write) – should not be confused with inline candidate.
    */
   public void testHookReadBeforeWrite() {
     testSame(
         "function f() { var a = 1; var b = g() ? (a = a + 2, 3) : (a = a + 3, 4); alert(a); }");
   }

   /**
    * Use after hook where the variable is assigned inside the hook but the hook
    * is the only definition reaching the use. Inlining should happen when RHS is constant.
    */
   public void testAfterHookUseConstantRHS() {
     test(
         "function f() { var a; var b = g() ? (a = 10, 20) : (a = 10, 30); alert(a); }",
         "function f() { var b = g() ? (20) : (30); alert(10); }");
   }

   // ---------- General inlining behaviour ----------

   /**
    * Simple variable that should be inlined.
    */
   public void testSimpleInline() {
     test(
         "function f() { var a = 42; alert(a); }",
         "function f() { alert(42); }");
   }

   /**
    * Variable with multiple assignments – no inlining.
    */
   public void testMultipleDefsNoInline() {
     testSame(
         "function f() { var a = 1; a = 2; alert(a); }");
   }

   /**
    * Variable assigned inside a simple if-else with same RHS – safe to inline.
    */
   public void testIfElseSameRHS() {
     test(
         "function f() { var a = 1; if (cond) { a = 7; } else { a = 7; } alert(a); }",
         "function f() { var a; if (cond) { a = 7; } else { a = 7; } alert(7); }");
   }

   /**
    * Variable exported via coding convention must not be inlined.
    */
   public void testExportedVarNotInlined() {
     testSame(
         "function f() { var a = 1; window['a'] = a; alert(a); }");
   }
 }